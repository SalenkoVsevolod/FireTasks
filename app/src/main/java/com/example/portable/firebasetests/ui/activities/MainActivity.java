package com.example.portable.firebasetests.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.listeners.AllTagsFirebaseListener;
import com.example.portable.firebasetests.ui.adapters.TagAdapter;
import com.example.portable.firebasetests.ui.fragments.DayFragment;
import com.example.portable.firebasetests.utils.StringUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Tag> tags;
    private Spinner tagSpinner;
    private TagAdapter tagAdapter;
    private DayFragment currentFragment;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        tabLayout = (TabLayout) findViewById(R.id.days_tabs);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.homeFloatingActionButton);
        floatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow)));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTaskModifier();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.tasksActivityToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        inflateDays(calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        tags = new ArrayList<>();
        tagAdapter = new TagAdapter(this, tags);
        tagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        tagSpinner.setAdapter(tagAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseListenersManager.getInstance().setAllTagsListener(new AllTagsFirebaseListener.OnTagsSyncListener() {
            @Override
            public void onSync(ArrayList<Tag> tagsArray) {
                tags.clear();
                tags.addAll(tagsArray);
                tagAdapter.notifyDataSetChanged();
            }
        });
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.getTabAt(getSelectionDay()).select();
                    }
                }, 100);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabViewSelected(tab, true);
                putNewFragment(tab.getPosition());
                Preferences.getInstance().writeWhenLastOpened(getCurrentDayOfYear());
                Preferences.getInstance().writeLastOpenedDay(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                setTabViewSelected(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (currentFragment != null) {
                    currentFragment.setSortingTagIdAndSort(((Tag) tagAdapter.getItem(position)).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int getSelectionDay() {
        int lastOpenedDay = Preferences.getInstance().readLastOpenedDay();
        int lastOpened = Preferences.getInstance().readWhenLastOpened();
        if (lastOpenedDay != -1 && lastOpened == getCurrentDayOfYear()) {
            return lastOpenedDay;
        } else {
            Preferences.getInstance().writeLastOpenedDay(getCurrentDayOfYear());
            Preferences.getInstance().writeWhenLastOpened(getCurrentDayOfYear());
            return getCurrentDayOfYear();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseListenersManager.getInstance().removeAllTagsListener();
    }

    private void putNewFragment(int dayOfYear) {
        currentFragment = DayFragment.newInstance(dayOfYear + 1);
        getFragmentManager().beginTransaction()
                .replace(R.id.day_of_week_container, currentFragment)
                .commit();
    }

    private int getCurrentDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.DAY_OF_YEAR) - 1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weeks_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutItem:
                showLogoutDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Logout?");
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.show();
    }

    @SuppressWarnings("all")
    private void logout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Preferences.getInstance().readUserId())
                .requestEmail()
                .build();
        final GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Preferences.getInstance().logout();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Toast.makeText(MainActivity.this, "connection suspended", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void inflateDays(int maxDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        for (int i = 1; i <= maxDays; i++) {
            calendar.set(Calendar.DAY_OF_YEAR, i);
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setCustomView(inflateDay(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_WEEK)));
            tabLayout.addTab(tab);
        }
    }

    private View inflateDay(int dayOfMonth, int dayOfWeek) {
        View v = getLayoutInflater().inflate(R.layout.item_day_number, null);
        TextView dayOfMonthText = (TextView) v.findViewById(R.id.day_of_month);
        TextView dayOfWeekText = (TextView) v.findViewById(R.id.day_of_week);
        dayOfMonthText.setText(dayOfMonth + "");
        dayOfWeekText.setText(StringUtils.getDayOfWeekName(dayOfWeek));
        return v;
    }

    private void startTaskModifier() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_YEAR, tabLayout.getSelectedTabPosition() + 1);
        Task task = new Task();
        task.getCalendar().set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
        task.getCalendar().set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        task.getCalendar().set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        TaskModifyActivity.start(MainActivity.this, task);
    }

    private void setTabViewSelected(TabLayout.Tab tab, boolean selected) {
        View v = tab.getCustomView();
        TextView week = (TextView) v.findViewById(R.id.day_of_week);
        TextView month = (TextView) v.findViewById(R.id.day_of_month);
        week.setTextColor(getTabColor(selected));
        month.setTextColor(getTabColor(selected));
    }

    private int getTabColor(boolean selected) {
        return selected ? Color.BLACK : ContextCompat.getColor(MainActivity.this, R.color.gray_inactive);
    }
}
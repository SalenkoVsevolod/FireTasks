package com.example.portable.firebasetests.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FirebaseObserver;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseExecutorManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.network.listeners.DefaultTagsStateTask;
import com.example.portable.firebasetests.ui.adapters.TagSortingSpinnerAdapter;
import com.example.portable.firebasetests.ui.fragments.DayFragment;
import com.example.portable.firebasetests.utils.StringUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class MainActivity extends BaseActivity {
    private TabLayout mTabLayout;
    private Spinner mTagSpinner;
    private ProgressBar mProgressBar;
    private TagSortingSpinnerAdapter mTagSortingSpinnerAdapter;
    private FloatingActionButton mAddFloatingActionButton;
    private DayFragment mCurrentFragment;
    private TabLayout.OnTabSelectedListener mTabSelectedListener;
    private int mBackPresses;
    private EntityList.FirebaseEntityListener<Tag> mTagsSyncListener;
    private EntityList.FirebaseEntityListener<Remind> mRemindsSyncListener;
    private View mSortingContainer, mDayContainer;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = (TabLayout) findViewById(R.id.days_tabs);
        mSortingContainer = findViewById(R.id.show_first_container);
        mDayContainer = findViewById(R.id.day_of_week_container);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAddFloatingActionButton = (FloatingActionButton) findViewById(R.id.homeFloatingActionButton);
        mAddFloatingActionButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add));
        mAddFloatingActionButton.setOnClickListener(new View.OnClickListener() {
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

        mTagSortingSpinnerAdapter = new TagSortingSpinnerAdapter(this, FirebaseObserver.getInstance().getTags());
        mTagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        mTagSpinner.setAdapter(mTagSortingSpinnerAdapter);
        mTabSelectedListener = new TabLayout.OnTabSelectedListener() {
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
        };
        FirebaseExecutorManager.getInstance().startTagsListener();
        new DefaultTagsStateTask(new DefaultTagsStateTask.DefaultTagsCreatedListener() {
            @Override
            public void created(boolean created) {
                if (!created) {
                    FirebaseUtils.getInstance().createDefaultTags();
                }
            }
        }).execute();
        mTagsSyncListener = new EntityList.FirebaseEntityListener<Tag>() {
            @Override
            public void onChanged(Tag tag) {
                mTagSortingSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCreated(Tag tag) {
                mTagSortingSpinnerAdapter.notifyDataSetChanged();
                setDataVisibility(true);
                mAddFloatingActionButton.show();
            }

            @Override
            public void onDeleted(Tag tag) {
                mTagSortingSpinnerAdapter.notifyDataSetChanged();
                mSortingContainer.setVisibility(FirebaseObserver.getInstance().getTags().size() == 0 ? View.GONE : View.VISIBLE);
            }
        };

        mRemindsSyncListener = new EntityList.FirebaseEntityListener<Remind>() {
            @Override
            public void onChanged(Remind remind) {
                if (remind.getCalendar().getTimeInMillis() > System.currentTimeMillis()) {
                    Notifier.removeAlarm(remind.getId());
                    Notifier.setAlarm(remind);
                } else {
                    FirebaseUtils.getInstance().removeReminder(remind.getCalendar().get(Calendar.DAY_OF_YEAR), remind.getTaskId(), remind.getId());
                }
            }

            @Override
            public void onCreated(Remind remind) {
                if (remind.getCalendar().getTimeInMillis() > System.currentTimeMillis()) {
                    Notifier.removeAlarm(remind.getId());
                    Notifier.setAlarm(remind);
                } else {
                    FirebaseUtils.getInstance().removeReminder(remind.getCalendar().get(Calendar.DAY_OF_YEAR), remind.getTaskId(), remind.getId());
                }
            }

            @Override
            public void onDeleted(Remind remind) {
                Notifier.removeAlarm(remind.getId());
                Preferences.getInstance().removeRemindCode(remind.getId());
            }
        };
        FirebaseObserver.getInstance().getTags().subscribe(mTagsSyncListener);
    }

    private void setDataVisibility(boolean visibility) {
        if (visibility) {
            mProgressBar.setVisibility(View.GONE);
            mDayContainer.setVisibility(View.VISIBLE);
            mSortingContainer.setVisibility(View.VISIBLE);
            mAddFloatingActionButton.show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mDayContainer.setVisibility(View.GONE);
            mSortingContainer.setVisibility(View.GONE);
            mAddFloatingActionButton.hide();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDataVisibility(FirebaseObserver.getInstance().getTags().size() > 0);
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        TabLayout.Tab tab = mTabLayout.getTabAt(getSelectionDay());
                        if (tab != null) {
                            tab.select();
                        }
                    }
                }, 100);
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);
        mTagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentFragment != null && mTagSortingSpinnerAdapter.getCount() != 0) {
                    mCurrentFragment.setSortingTagIdAndSort(((Tag) mTagSortingSpinnerAdapter.getItem(position)).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TabLayout.Tab tab = mTabLayout.getTabAt(getSelectionDay());
                if (tab != null) {
                    tab.select();
                }
            }
        });
        mBackPresses = 0;
        FirebaseObserver.getInstance().getReminders().subscribe(mRemindsSyncListener);
        FirebaseExecutorManager.getInstance().startRemindersListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTabLayout.removeOnTabSelectedListener(mTabSelectedListener);
        FirebaseObserver.getInstance().getReminders().unsubscribe(mRemindsSyncListener);
        FirebaseExecutorManager.getInstance().stopRemindersListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseObserver.getInstance().getTags().unsubscribe(mTagsSyncListener);
        FirebaseExecutorManager.getInstance().stopRemindersListener();
        FirebaseExecutorManager.getInstance().stopTagsListener();
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

    private void putNewFragment(int dayOfYear) {
        int position = mTagSpinner.getSelectedItemPosition();
        if (position != -1) {
            mCurrentFragment = DayFragment.newInstance(dayOfYear + 1, ((Tag) mTagSortingSpinnerAdapter.getItem(position)).getId());
        } else {
            mCurrentFragment = DayFragment.newInstance(dayOfYear + 1, null);
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.day_of_week_container, mCurrentFragment)
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
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.logout_from_app);
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
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                showToast(getString(R.string.internet_connection_error), true);
            }
        });

    }

    private void inflateDays(int maxDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        for (int i = 1; i <= maxDays; i++) {
            calendar.set(Calendar.DAY_OF_YEAR, i);
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setCustomView(inflateDay(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.DAY_OF_WEEK)));
            mTabLayout.addTab(tab);
        }
    }

    private View inflateDay(int dayOfMonth, int dayOfWeek) {
        View v = getLayoutInflater().inflate(R.layout.item_day_number, null);
        TextView dayOfMonthText = (TextView) v.findViewById(R.id.day_of_month);
        TextView dayOfWeekText = (TextView) v.findViewById(R.id.day_of_week);
        String day = dayOfMonth + "";
        dayOfMonthText.setText(day);
        dayOfWeekText.setText(StringUtils.getDayOfWeekName(dayOfWeek));
        return v;
    }

    private void startTaskModifier() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_YEAR, mTabLayout.getSelectedTabPosition() + 1);
        Task task = new Task();
        task.getCalendar().set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
        task.getCalendar().set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));
        task.getCalendar().set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        TaskEditActivity.start(MainActivity.this, task);
    }

    private void setTabViewSelected(TabLayout.Tab tab, boolean selected) {
        View v = tab.getCustomView();
        if (v != null) {
            TextView week = (TextView) v.findViewById(R.id.day_of_week);
            TextView month = (TextView) v.findViewById(R.id.day_of_month);
            week.setTextColor(getTabColor(selected));
            month.setTextColor(getTabColor(selected));
        }
    }

    private int getTabColor(boolean selected) {
        return selected ? Color.BLACK : ContextCompat.getColor(MainActivity.this, R.color.gray_inactive);
    }

    @Override
    public void onBackPressed() {
        boolean handled = mCurrentFragment.hideDeleting();
        if (!handled) {
            exit();
        }
    }

    private void exit() {
        mBackPresses++;
        if (mBackPresses > 1) {
            finish();
        } else {
            showToast(getString(R.string.press_back_again), true);
        }
    }

}
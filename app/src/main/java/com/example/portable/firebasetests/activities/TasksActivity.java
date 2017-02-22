package com.example.portable.firebasetests.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portable.firebasetests.AppCompatColors;
import com.example.portable.firebasetests.MySharedPreferences;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.StringUtils;
import com.example.portable.firebasetests.adapters.WeeksPagerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class TasksActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private TextView weekBoundsTextView;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        final ViewPager pager = (ViewPager) findViewById(R.id.tasksViewPager);
        pager.setAdapter(new WeeksPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(getCurrentWeekOfYearPosition());
        floatingActionButton = (FloatingActionButton) findViewById(R.id.homeFloatingActionButton);
        floatingActionButton.hide();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(getCurrentWeekOfYearPosition());
            }
        });
        weekBoundsTextView = (TextView) findViewById(R.id.weekBoundsTextView);
        String currentWeekBounds = "" + getCurrentWeekOfYearPosition();
        weekBoundsTextView.setText(getWeekBoundsString(getCurrentWeekOfYearPosition()));
        pager.addOnPageChangeListener(getOnPageChangeListener());
        Toolbar toolbar = (Toolbar) findViewById(R.id.tasksActivityToolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(AppCompatColors.getColor(R.color.titleText, this));
        setSupportActionBar(toolbar);
    }

    private ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == getCurrentWeekOfYearPosition()) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
                weekBoundsTextView.setText(getWeekBoundsString(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };


    }

    private int getCurrentWeekOfYearPosition() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.WEEK_OF_YEAR) - 1;
    }

    private String getWeekBoundsString(int weekOfYear) {
        weekOfYear++;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        String firstDay = StringUtils.formatDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        String lastDay = StringUtils.formatDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1);
        return firstDay + "-" + lastDay;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items, menu);
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
                .requestIdToken(MySharedPreferences.readUserId(this))
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
                                MySharedPreferences.logout(TasksActivity.this);
                                startActivity(new Intent(TasksActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Toast.makeText(TasksActivity.this, "connection suspended", Toast.LENGTH_LONG).show();
            }
        });
    }
}
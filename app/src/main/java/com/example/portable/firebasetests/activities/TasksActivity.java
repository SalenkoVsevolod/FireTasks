package com.example.portable.firebasetests.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.adapters.WeeksPagerAdapter;

import java.util.Calendar;

public class TasksActivity extends AppCompatActivity {
    private ViewPager pager;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        pager = (ViewPager) findViewById(R.id.tasksViewPager);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        WeeksPagerAdapter adapter = new WeeksPagerAdapter(getSupportFragmentManager(), calendar.get(Calendar.WEEK_OF_YEAR));
        pager.setAdapter(adapter);
        pager.setCurrentItem(calendar.get(Calendar.WEEK_OF_YEAR));

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutItem:
                showLogOutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @SuppressWarnings("all")
    private void logout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getSharedPreferences(getString(R.string.sharedPreferencesFileName), MODE_PRIVATE).getString(getString(R.string.ID_SHARED_KEY), null))
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
                                getSharedPreferences(getString(R.string.sharedPreferencesFileName), MODE_PRIVATE).edit().remove(getString(R.string.ID_SHARED_KEY)).commit();
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

    private void showLogOutDialog() {
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
    }*/
}

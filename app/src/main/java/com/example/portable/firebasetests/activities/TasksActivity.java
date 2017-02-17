package com.example.portable.firebasetests.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.adapters.WeeksPagerAdapter;

import java.util.Calendar;

public class TasksActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        final ViewPager pager = (ViewPager) findViewById(R.id.tasksViewPager);
        WeeksPagerAdapter adapter = new WeeksPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(getCurrentWeekOfYearPosition());
        floatingActionButton = (FloatingActionButton) findViewById(R.id.homeFloatingActionButton);
        floatingActionButton.hide();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(getCurrentWeekOfYearPosition());
            }
        });
        pager.addOnPageChangeListener(getOnPageChangeListener());
    }

    private ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("page", "page selected:" + position + " current week:" + getCurrentWeekOfYearPosition());
                if (position == getCurrentWeekOfYearPosition()) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
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
}
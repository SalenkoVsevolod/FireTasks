package com.example.portable.firebasetests.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.StringUtils;
import com.example.portable.firebasetests.adapters.WeeksPagerAdapter;

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
        weekBoundsTextView = (TextView) findViewById(R.id.weekBoundsTextView);
        String currentWeekBounds = "" + getCurrentWeekOfYearPosition();
        weekBoundsTextView.setText(getWeekBoundsString(getCurrentWeekOfYearPosition()));
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
        Log.i("weekBounds", "input week of year:" + weekOfYear);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        String firstDay = StringUtils.formatDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        String lastDay = StringUtils.formatDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR));
        Log.i("weekBounds", firstDay + "-" + lastDay);
        return firstDay + "-" + lastDay;
    }
}
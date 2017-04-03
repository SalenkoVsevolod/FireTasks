package com.example.portable.firebasetests.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.portable.firebasetests.ui.fragments.DayNumberFragment;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 16.02.2017.
 */

public class WeeksPagerAdapter extends FragmentPagerAdapter {
    private int days;


    public WeeksPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        days = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    @Override
    public Fragment getItem(int position) {
        return DayNumberFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return days;
    }
}

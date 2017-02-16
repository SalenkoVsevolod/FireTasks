package com.example.portable.firebasetests.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.portable.firebasetests.fragments.TasksWeekFragment;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 16.02.2017.
 */

public class WeeksPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments;

    public WeeksPagerAdapter(FragmentManager fm, int nowWeek) {
        super(fm);
        fragments = new ArrayList<>();
        for (int i = 1; i <= nowWeek; i++) {
            fragments.add(TasksWeekFragment.newInstance(i));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}

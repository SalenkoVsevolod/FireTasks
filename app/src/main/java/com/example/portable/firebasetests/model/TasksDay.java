package com.example.portable.firebasetests.model;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.example.portable.firebasetests.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class TasksDay implements ParentObject {
    private Calendar calendar;
    private List<Object> children;

    public TasksDay(int year, int weekOfYear, int realDay) {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        calendar.roll(Calendar.DAY_OF_WEEK, realDay);
        children = new ArrayList<>();
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public int getWeek() {
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public List<Object> getChildObjectList() {
        return children;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        children = list;
    }

    public void addChild(Object o) {
        ArrayList<Object> res = new ArrayList<>(getChildObjectList());
        res.add(o);
        setChildObjectList(res);
    }

    public String getDateString() {
        return StringUtils.formatDate(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    public String getName() {
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
    }

    @Override
    public String toString() {
        return getName();
    }
}

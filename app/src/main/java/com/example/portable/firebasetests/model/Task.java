package com.example.portable.firebasetests.model;


import com.example.portable.firebasetests.utils.StringUtils;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Portable on 24.01.2017.
 */

@SuppressWarnings("unchecked")
public class Task implements Serializable {
    private String name, description;
    private boolean timeSpecified;
    private ArrayList<SubTask> subTasks;
    private String id;
    private long tagIndex;
    private Calendar calendar;

    public Task() {
        description = "";
        name = "";
        subTasks = new ArrayList<>();
        calendar = Calendar.getInstance();
    }

    public Task(HashMap<String, Object> map) {
        this();
        name = (String) map.get("name");
        description = (String) map.get("description");
        calendar.setTimeInMillis((long) map.get("timeStamp"));
        timeSpecified = (boolean) map.get("timeSpecified");
        tagIndex = (long) map.get("tagIndex");
        subTasks = getSubTasks((HashMap<String, Object>) map.get("subTasks"));
    }

    private ArrayList<SubTask> getSubTasks(HashMap<String, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        ArrayList<SubTask> subTasks = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            SubTask subTask = new SubTask((HashMap<String, Object>) map.get(key));
            subTask.setId(key);
            subTasks.add(subTask);
        }
        return subTasks;
    }

    @Exclude
    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public boolean isCompleted() {
        for (SubTask subTask : subTasks) {
            if (!subTask.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Exclude
    public String getTimeString() {
        return StringUtils.formatNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + StringUtils.formatNumber(calendar.get(Calendar.MINUTE));
    }

    public boolean isTimeSpecified() {
        return timeSpecified;
    }

    public void setTimeSpecified() {
        this.timeSpecified = true;
    }

    public long getTimeStamp() {
        return calendar.getTimeInMillis();
    }

    public void setTimeStamp(long timeStamp) {
        calendar.setTimeInMillis(timeStamp);
    }

    @Exclude
    public Calendar getCalendar() {
        return calendar;
    }

    @Override
    public String toString() {
        return description;
    }

    public long getTagIndex() {
        return tagIndex;
    }

    public void setTagIndex(int tagIndex) {
        this.tagIndex = tagIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.example.portable.firebasetests.model;


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
    private String description;
    private boolean timeSpecified;
    private long timeStamp;
    private ArrayList<SubTask> subTasks;
    private String id;
    private Tag tag;

    public Task() {
        subTasks = new ArrayList<>();
    }

    public Task(HashMap<String, Object> map) {
        this();
        description = (String) map.get("description");
        timeStamp = (long) map.get("timeStamp");
        timeSpecified = (boolean) map.get("timeSpecified");
        tag = new Tag((HashMap<String, Object>) map.get("tag"));
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

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
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
    public String getDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(timeStamp);
        return formatNumber(calendar.get(Calendar.DAY_OF_MONTH)) + "." + formatNumber(calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
    }

    private String formatNumber(int num) {
        return (num < 10 ? "0" : "") + num;
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
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(timeStamp);
        return formatNumber(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + formatNumber(calendar.get(Calendar.MINUTE));
    }

    public boolean isTimeSpecified() {
        return timeSpecified;
    }

    public void setTimeSpecified(boolean timeSpecified) {
        this.timeSpecified = timeSpecified;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}

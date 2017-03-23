package com.example.portable.firebasetests.model;


import com.example.portable.firebasetests.utils.StringUtils;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Portable on 24.01.2017.
 */

@SuppressWarnings("unchecked")
public class Task implements Serializable {
    private String name, description;
    private ArrayList<SubTask> subTasks;
    private String id;
    private long tagIndex;
    private Calendar calendar;
    private ArrayList<Remind> reminds;

    public Task() {
        description = "";
        name = "";
        reminds = new ArrayList<>();
        subTasks = new ArrayList<>();
        calendar = Calendar.getInstance();
    }

    public Task(HashMap<String, Object> map) {
        this();
        name = (String) map.get("name");
        description = (String) map.get("description");
        calendar.setTimeInMillis((long) map.get("timeStamp"));
        tagIndex = (long) map.get("tagIndex");
        subTasks = getSubTasks((HashMap<String, Object>) map.get("subTasks"));
        reminds = getReminders((HashMap<String, Object>) map.get("reminds"));
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

    private ArrayList<Remind> getReminders(HashMap<String, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        ArrayList<Remind> res = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Remind remind = new Remind();
            remind.setTimeStamp(Long.parseLong(key));
            remind.setVibro(Boolean.parseBoolean(map.get(key).toString()));
            res.add(remind);
        }
        return res;
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


    public long getTimeStamp() {
        return calendar.getTimeInMillis();
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

    @Exclude
    public ArrayList<Remind> getReminds() {
        return reminds;
    }

    public void setReminds(ArrayList<Remind> reminds) {
        this.reminds = reminds;
    }
}

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
    private String name, description;
    private ArrayList<SubTask> subTasks;
    private String id;
    private String tagId;
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
        tagId = (String) map.get("tagId");
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
            Remind remind = new Remind((HashMap<String, Object>) map.get(key));
            remind.setId(key);
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

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    @Exclude
    public int getProgress() {
        float done = 0;
        float sum = 0;
        for (SubTask s : getSubTasks()) {
            sum += (s.getPriority() + 1);
            if (s.isDone()) {
                done += (s.getPriority() + 1);
            }
        }
        return (int) ((done / sum) * 100);
    }
}

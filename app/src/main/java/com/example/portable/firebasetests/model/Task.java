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
    private Calendar calendar;
    private ArrayList<Remind> reminds;
    private Tag tag;

    public Task() {
        description = "";
        name = "";
        reminds = new ArrayList<>();
        subTasks = new ArrayList<>();
        calendar = Calendar.getInstance();
    }

    public Task(HashMap<String, Object> map) {
        this();
        setData(map);
    }

    public void setData(Task task) {
        name = task.getName();
        description = task.getDescription();
        calendar.setTimeInMillis(task.getTimeStamp());
        tag = task.getTag();
        subTasks.clear();
        subTasks.addAll(task.getSubTasks());
        reminds.clear();
        reminds.addAll(task.getReminds());
    }

    public void setData(HashMap<String, Object> map) {
        name = (String) map.get("name");
        description = (String) map.get("description");
        calendar.setTimeInMillis((long) map.get("timeStamp"));
        tag = new Tag((HashMap<String, Object>) map.get("tag"));
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

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public int getDayOfYear() {
        return calendar.get(Calendar.DAY_OF_YEAR);
    }
}

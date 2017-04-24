package com.example.portable.firebasetests.model;


import android.util.Log;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Portable on 24.01.2017.
 */

@SuppressWarnings("unchecked")
public class Task extends FirebaseEntity {
    private String name, description;
    private ArrayList<SubTask> subTasks;
    private String tagId;
    private Calendar calendar;
    private ArrayList<String> reminds;

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
        parseSubTasks((HashMap<String, Object>) map.get("subTasks"));
        parseReminds((HashMap<String, Object>) map.get("reminders"));
    }

    @Override
    public void init(FirebaseEntity entity) {
        Task task = (Task) entity;
        name = task.getName();
        description = task.getDescription();
        tagId = task.getTagId();
        reminds.clear();
        reminds.addAll(task.getReminds());
        calendar.setTimeInMillis(task.getCalendar().getTimeInMillis());
        subTasks.clear();
        subTasks.addAll(task.getSubTasks());
    }

    private void parseReminds(HashMap<String, Object> rawData) {
        if (rawData != null) {
            for (String id : rawData.keySet()) {
                reminds.add(id);
            }
        }
    }

    private void parseSubTasks(HashMap<String, Object> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        Set<String> keys = map.keySet();
        for (String key : keys) {
            SubTask subTask = new SubTask((HashMap<String, Object>) map.get(key));
            subTask.setId(key);
            subTasks.add(subTask);
        }
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
    public ArrayList<String> getReminds() {
        return reminds;
    }

    public void setReminds(ArrayList<String> reminds) {
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

    @Override
    public boolean isIdentical(FirebaseEntity entity) {
        Task task = (Task) entity;
        Log.i("fireSync", "checking task " + task.getName() + " for identical");
        if (subTasks.size() != task.getSubTasks().size()) {
            return false;
        }
        for (int i = 0; i < subTasks.size(); i++) {
            if (!subTasks.get(i).isIdentical(task.getSubTasks().get(i))) {
                return false;
            }
        }
        Log.i("fireSync", "subtasks are identical");
        for (int i = 0; i < reminds.size(); i++) {
            if (!reminds.get(i).equals(task.getReminds().get(i))) {
                return false;
            }
        }
        if (reminds.size() != task.getReminds().size()) {
            return false;
        }
        return name.equals(task.getName())
                && description.equals(task.getDescription())
                && tagId.equals(task.getTagId())
                && calendar.getTimeInMillis() == task.getCalendar().getTimeInMillis();
    }
}

package com.example.portable.firebasetests.model;

import com.example.portable.firebasetests.R;
import com.google.firebase.database.Exclude;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Portable on 24.01.2017.
 */

public class SubTask extends FirebaseEntity {
    public static final List<Integer> PRIORITY_COLORS_IDS = Arrays.asList(R.color.low, R.color.medium, R.color.high, R.color.urgent);
    private String name;
    private boolean done;
    private long priority;
    private String id;

    public SubTask(HashMap<String, Object> map) {
        done = (boolean) map.get("done");
        name = (String) map.get("name");
        priority = (long) map.get("priority");
    }

    public SubTask() {
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void init(FirebaseEntity entity) {
        SubTask subTask = (SubTask) entity;
        name = subTask.getName();
        done = subTask.isDone();
        priority = subTask.getPriority();
    }

    @Override
    public boolean isIdentical(FirebaseEntity entity) {
        SubTask subTask = (SubTask) entity;
        return name.equals(subTask.getName()) && done == subTask.isDone() && priority == subTask.getPriority();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SubTask)) {
            return false;
        }
        SubTask s = (SubTask) obj;
        return s.getId().equals(id);
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

}

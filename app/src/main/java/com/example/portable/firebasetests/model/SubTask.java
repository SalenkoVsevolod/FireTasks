package com.example.portable.firebasetests.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Portable on 24.01.2017.
 */

public class SubTask implements Serializable {
    public static final List<String> PRIORITIES = Arrays.asList("Law", "Normal", "High", "Urgent");
    private String description;
    private boolean done;
    private long priority;
    private String id;

    public SubTask(HashMap<String, Object> map) {
        done = (boolean) map.get("done");
        description = (String) map.get("description");
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

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }
}

package com.example.portable.firebasetests.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by Portable on 24.01.2017.
 */

public class SubTask {
    private String description;
    private boolean done;
    private String id;

    public SubTask(HashMap<String, Object> map) {
        done = (boolean) map.get("done");
        description = (String) map.get("description");
    }

    public SubTask(String description) {
        this.description = description;
        done = false;
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
}

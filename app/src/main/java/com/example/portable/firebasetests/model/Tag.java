package com.example.portable.firebasetests.model;

import android.util.SparseArray;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Salenko Vsevolod on 16.02.2017.
 */

public class Tag extends FirebaseEntity {
    public static final List<Tag> DEFAULT_TAGS = Arrays.asList(
            new Tag("default_1", FireTasksApp.getInstance().getResources().getStringArray(R.array.default_tags)[0], FireTasksApp.getInstance().getCompatColor(R.color.work_tag)),
            new Tag("default_2", FireTasksApp.getInstance().getResources().getStringArray(R.array.default_tags)[1], FireTasksApp.getInstance().getCompatColor(R.color.home_tag)),
            new Tag("default_3", FireTasksApp.getInstance().getResources().getStringArray(R.array.default_tags)[2], FireTasksApp.getInstance().getCompatColor(R.color.travel_tag)));

    private String name;
    private long color;
    private transient SparseArray<ArrayList<String>> tasks;

    public Tag() {
        tasks = new SparseArray<>();
    }


    public Tag(HashMap<String, Object> map) {
        this();
        name = (String) map.get("name");
        color = (long) map.get("color");
        parseDays((HashMap<String, Object>) map.get("tasks"));
    }

    public Tag(String id, String name, long color) {
        this();
        this.name = name;
        this.color = color;
        this.id = id;
    }

    private void parseDays(HashMap<String, Object> map) {
        if (map != null) {
            for (String day : map.keySet()) {
                int d = Integer.parseInt(day);
                parseTask(d, (HashMap<String, Objects>) map.get(day));
            }
        }
    }

    private void parseTask(int day, HashMap<String, Objects> map) {
        for (String taskId : map.keySet()) {
            if (tasks.get(day) == null) {
                tasks.put(day, new ArrayList<String>());
            }
            tasks.get(day).add(taskId);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getColor() {
        return color;
    }

    public void setColor(long color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag t = (Tag) obj;
        return t.getId().equals(id);
    }

    @Override
    public void init(FirebaseEntity entity) {
        Tag tag = (Tag) entity;
        name = tag.getName();
        color = tag.getColor();
        tasks = tag.getTasks();
    }

    @Override
    public boolean isIdentical(FirebaseEntity entity) {
        Tag tag = (Tag) entity;
        return tag.getColor() == color && tag.getName().equals(name) && tasksIsIdentical(tag.getTasks());
    }

    private boolean tasksIsIdentical(SparseArray<ArrayList<String>> tasksInput) {
        if (tasks.size() != tasksInput.size()) {
            return false;
        }
        for (int i = 0; i < tasks.size(); i++) {
            ArrayList<String> day = tasks.get(tasks.keyAt(i));
            ArrayList<String> inputDay = tasksInput.get(tasksInput.keyAt(i));
            if (day.size() != inputDay.size()) {
                return false;
            }
            for (String s : day) {
                if (!inputDay.contains(s)) {
                    return false;
                }
            }
            for (String s : inputDay) {
                if (!day.contains(s)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Exclude
    public SparseArray<ArrayList<String>> getTasks() {
        return tasks;
    }

    public void setTasks(SparseArray<ArrayList<String>> tasks) {
        this.tasks = tasks;
    }
}

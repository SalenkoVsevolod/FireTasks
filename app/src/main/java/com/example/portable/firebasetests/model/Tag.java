package com.example.portable.firebasetests.model;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Salenko Vsevolod on 16.02.2017.
 */

public class Tag extends FirebaseEntity {
    public static final List<Tag> DEFAULT_TAGS = Arrays.asList(
            new Tag("default_1", "Work", FireTasksApp.getInstance().getCompatColor(R.color.work_tag)),
            new Tag("default_2", "Home", FireTasksApp.getInstance().getCompatColor(R.color.home_tag)),
            new Tag("default_3", "Travel", FireTasksApp.getInstance().getCompatColor(R.color.travel_tag)));

    private String name;
    private long color;

    public Tag() {
    }


    public Tag(HashMap<String, Object> map) {
        name = (String) map.get("name");
        color = (long) map.get("color");
    }

    public Tag(String id, String name, long color) {
        this.name = name;
        this.color = color;
        this.id = id;
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
        Tag t = (Tag) obj;
        return t.getId().equals(id);
    }

    @Override
    public void init(FirebaseEntity entity) {
        Tag tag = (Tag) entity;
        name = tag.getName();
        color = tag.getColor();
    }

    @Override
    public boolean isIdentical(FirebaseEntity entity) {
        Tag tag = (Tag) entity;
        return tag.getColor() == color && tag.getName().equals(name);
    }
}

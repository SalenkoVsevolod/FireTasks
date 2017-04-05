package com.example.portable.firebasetests.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Salenko Vsevolod on 16.02.2017.
 */

public class Tag implements Serializable {
    private String name;
    private String id;
    private long color;

    public Tag() {
    }

    public Tag(HashMap<String, Object> map) {
        name = (String) map.get("name");
        color = (long) map.get("color");
    }

    @Exclude
    public static Tag getTagById(String id, List<Tag> tags) {
        if (tags != null) {
            for (Tag t : tags) {
                if (t.getId().equals(id)) {
                    return t;
                }
            }
        }
        return null;
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

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        Tag t = (Tag) obj;
        return t.getColor() == color && t.getName().equals(name);
    }
}

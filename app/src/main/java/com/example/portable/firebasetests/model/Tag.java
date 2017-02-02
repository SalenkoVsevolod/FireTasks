package com.example.portable.firebasetests.model;

import java.util.HashMap;

/**
 * Created by Portable on 26.01.2017.
 */

public class Tag {
    private String name;
    private long color;

    public Tag(HashMap<String, Object> map) {
        name = (String) map.get("name");
        color = (long) map.get("color");
    }

    public Tag(String name, int color) {
        this.name = name;
        this.color = color;
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

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tag newTag = (Tag) obj;
        return name.equals(newTag.name);


    }
}

package com.example.portable.firebasetests.model;

import com.example.portable.firebasetests.network.FirebaseEntity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 16.02.2017.
 */

public class Tag extends FirebaseEntity implements Serializable {
    private String name;
    private long color;

    public Tag() {
    }


    public Tag(HashMap<String, Object> map) {
        name = (String) map.get("name");
        color = (long) map.get("color");
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

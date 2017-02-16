package com.example.portable.firebasetests.model;

/**
 * Created by Salenko Vsevolod on 16.02.2017.
 */

public class Tag {
    private String name;
    private int index, color;

    public Tag(int index, String name, int color) {
        this.name = name;
        this.index = index;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

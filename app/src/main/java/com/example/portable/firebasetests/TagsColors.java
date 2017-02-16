package com.example.portable.firebasetests;

import android.graphics.Color;

import com.example.portable.firebasetests.model.Tag;

import java.util.ArrayList;

/**
 * Created by Portable on 26.01.2017.
 */

public class TagsColors {
    private static ArrayList<Tag> tags;

    static {
        tags = new ArrayList<>();
        tags.add(new Tag(tags.size(), "work", Color.RED));
        tags.add(new Tag(tags.size(), "family", Color.BLUE));
        tags.add(new Tag(tags.size(), "travel", Color.GREEN));
    }

    public static int getTagColor(int index) {
        return tags.get(index).getColor();
    }

    public static ArrayList<Tag> getTags() {
        return tags;
    }
}

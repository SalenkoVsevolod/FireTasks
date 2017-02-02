package com.example.portable.firebasetests;

import android.graphics.Color;

import com.example.portable.firebasetests.model.Tag;

import java.util.ArrayList;

/**
 * Created by Portable on 26.01.2017.
 */

public class PredefinedTags {
    private static ArrayList<Tag> tags;

    static {
        tags = new ArrayList<>();
        tags.add(new Tag("work", Color.RED));
        tags.add(new Tag("family", Color.BLUE));
        tags.add(new Tag("travel", Color.GREEN));
    }

    public static ArrayList<Tag> getTags() {
        return tags;
    }
}

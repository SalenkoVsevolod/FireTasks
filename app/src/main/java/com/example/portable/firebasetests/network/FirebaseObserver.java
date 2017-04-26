package com.example.portable.firebasetests.network;

import android.util.SparseArray;

import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;

/**
 * Created by Salenko Vsevolod on 20.04.2017.
 */

public class FirebaseObserver {
    private static FirebaseObserver instance;

    private final EntityList<Tag> tags = new EntityList<>();
    private final SparseArray<EntityList<Task>> tasks = new SparseArray<>();
    private final EntityList<Remind> reminders = new EntityList<>();

    private FirebaseObserver() {
    }

    public static FirebaseObserver getInstance() {
        if (instance == null) {
            instance = new FirebaseObserver();
        }
        return instance;
    }

    public EntityList<Tag> getTags() {
        return tags;
    }

    public EntityList<Task> getTasksDay(int day) {
        if (tasks.get(day) == null) {
            tasks.put(day, new EntityList<Task>());
        }
        return tasks.get(day);
    }

    public EntityList<Remind> getReminders() {
        return reminders;
    }
}
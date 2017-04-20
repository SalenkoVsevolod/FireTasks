package com.example.portable.firebasetests.core;

import android.util.SparseArray;

import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseEntity;

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

    public void syncTags(EntityList<Tag> syncedTags) {
        tags.sync(syncedTags);
    }

    public void syncTasksDay(int day, EntityList<Task> syncedTasks) {
        if (tasks.get(day) == null) {
            tasks.put(day, new EntityList<Task>());
        }
        tasks.get(day).sync(syncedTasks);
    }


    public void addOnTaskCreatedListener(int day, OnEntityCreatedListener listener) {
        if (tasks.get(day) != null) {
            tasks.get(day).getCreatedListeners().add(listener);
        }
    }

    public void syncReminders(EntityList<Remind> syncedReminds) {
        reminders.sync(syncedReminds);
    }

    public void addOnReminderCreatedLIstener(OnEntityCreatedListener listener) {
        reminders.getCreatedListeners().add(listener);
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

    public interface OnEntityCreatedListener {
        void onCreated(FirebaseEntity entity);
    }
}
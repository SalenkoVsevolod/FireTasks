package com.example.portable.firebasetests.network;

import android.util.SparseArray;

import com.example.portable.firebasetests.network.listeners.RemindersSyncTask;
import com.example.portable.firebasetests.network.listeners.TagsSyncTask;
import com.example.portable.firebasetests.network.listeners.TaskDaySyncTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class FirebaseExecutorManager {
    private static FirebaseExecutorManager instance;
    private ExecutorService tagsService, remindersService;
    private SparseArray<ExecutorService> daysExecutors;

    private FirebaseExecutorManager() {
        daysExecutors = new SparseArray<>();
    }

    public static FirebaseExecutorManager getInstance() {
        if (instance == null) {
            instance = new FirebaseExecutorManager();
        }
        return instance;
    }

    public void startTagsListener() {
        tagsService = Executors.newSingleThreadExecutor();
        tagsService.submit(new TagsSyncTask());
    }

    public void stopTagsListener() {
        tagsService.shutdownNow();
    }

    public void startRemindersListener() {
        remindersService = Executors.newSingleThreadExecutor();
        remindersService.submit(new RemindersSyncTask());
    }

    public void stopRemindersListener() {
        remindersService.shutdownNow();
    }

    public void startDayListener(int day) {
        daysExecutors.put(day, Executors.newSingleThreadExecutor());
        daysExecutors.get(day).submit(new TaskDaySyncTask(day));
    }

    public void stopDayListener(int day) {
        daysExecutors.get(day).shutdownNow();
    }
}

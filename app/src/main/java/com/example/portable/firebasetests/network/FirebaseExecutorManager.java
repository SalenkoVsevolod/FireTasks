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
    private ExecutorService mTagsService, mRemindersService;
    private SparseArray<ExecutorService> mDaysExecutors;

    private FirebaseExecutorManager() {
        mDaysExecutors = new SparseArray<>();
    }

    public static FirebaseExecutorManager getInstance() {
        if (instance == null) {
            instance = new FirebaseExecutorManager();
        }
        return instance;
    }

    public void startTagsListener() {
        mTagsService = Executors.newSingleThreadExecutor();
        mTagsService.submit(new TagsSyncTask());
    }

    public void stopTagsListener() {
        mTagsService.shutdownNow();
    }

    public void startRemindersListener() {
        mRemindersService = Executors.newSingleThreadExecutor();
        mRemindersService.submit(new RemindersSyncTask());
    }

    public void stopRemindersListener() {
        mRemindersService.shutdownNow();
    }

    public void startDayListener(int day) {
        mDaysExecutors.put(day, Executors.newSingleThreadExecutor());
        mDaysExecutors.get(day).submit(new TaskDaySyncTask(day));
    }

    public void stopDayListener(int day) {
        mDaysExecutors.get(day).shutdownNow();
    }
}

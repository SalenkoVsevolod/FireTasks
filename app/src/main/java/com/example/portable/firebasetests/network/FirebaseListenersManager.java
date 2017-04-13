package com.example.portable.firebasetests.network;

import android.util.SparseArray;

import com.example.portable.firebasetests.network.listeners.DayFirebaseListener;
import com.example.portable.firebasetests.network.listeners.TagsFirebaseListener;
import com.example.portable.firebasetests.network.listeners.TaskFirebaseListener;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class FirebaseListenersManager extends FirebaseMediator {
    private static FirebaseListenersManager instance;
    private SparseArray<DayFirebaseListener> dayListeners;
    private TagsFirebaseListener tagsFirebaseListener;
    private TaskFirebaseListener taskFirebaseListener;

    private FirebaseListenersManager() {
        dayListeners = new SparseArray<>();
    }

    public static FirebaseListenersManager getInstance() {
        if (instance == null) {
            instance = new FirebaseListenersManager();
        }
        return instance;
    }


    public void setTagsListener(TagsFirebaseListener.OnTagsSyncListener listener) {
        tagsFirebaseListener = new TagsFirebaseListener(listener);
        tagsFirebaseListener.execute();
    }

    public void removeTagsListener() {
        tagsFirebaseListener.cancel(true);
        tagsFirebaseListener = null;
    }

    public void setDayListener(int day, DayFirebaseListener.DataChangingListener listener) {
        DayFirebaseListener task = new DayFirebaseListener(day, listener);
        dayListeners.put(day, task);
        task.execute();
    }

    public void removeDayListener(int day) {
        dayListeners.get(day).cancel(true);
        dayListeners.remove(day);
    }

    public void setTaskFirebaseListener(int day, String id, TaskFirebaseListener.OnTaskChangingListener listener) {
        taskFirebaseListener = new TaskFirebaseListener(day, id, listener);
        taskFirebaseListener.execute();
    }

    public void removeTaskFirebaseListener() {
        if (taskFirebaseListener != null) {
            taskFirebaseListener.cancel(true);
            taskFirebaseListener = null;
        }
    }
}
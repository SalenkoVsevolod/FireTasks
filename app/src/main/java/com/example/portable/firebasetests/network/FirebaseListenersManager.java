package com.example.portable.firebasetests.network;

import android.util.SparseArray;

import com.example.portable.firebasetests.network.listeners.AllTagsFirebaseListener;
import com.example.portable.firebasetests.network.listeners.DayFirebaseListener;
import com.example.portable.firebasetests.network.listeners.TagFirebaseListener;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class FirebaseListenersManager extends FirebaseMediator {
    private static FirebaseListenersManager instance;
    private SparseArray<DayFirebaseListener> dayListeners;
    private AllTagsFirebaseListener allTagsFirebaseListener;
    private TagFirebaseListener tagFirebaseListener;

    private FirebaseListenersManager() {
        dayListeners = new SparseArray<>();
    }

    public static FirebaseListenersManager getInstance() {
        if (instance == null) {
            instance = new FirebaseListenersManager();
        }
        return instance;
    }


    public void setAllTagsListener(AllTagsFirebaseListener.OnTagsSyncListener listener) {
        allTagsFirebaseListener = new AllTagsFirebaseListener(listener);
        allTagsFirebaseListener.execute();
    }

    public void removeAllTagsListener() {
        if (allTagsFirebaseListener != null) {
            allTagsFirebaseListener.cancel(true);
        }
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

    public void setTagListener(String tagId, TagFirebaseListener.OnTagGetListener listener) {
        tagFirebaseListener = new TagFirebaseListener(tagId, listener);
        tagFirebaseListener.execute();
    }

    public void removeTagListener() {
        if (tagFirebaseListener != null) {
            tagFirebaseListener.cancel(true);
        }
    }
}

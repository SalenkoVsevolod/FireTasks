package com.example.portable.firebasetests.model;

import android.util.SparseArray;

/**
 * Created by Black on 19.04.2017.
 */

public abstract class FirebaseObservableEntity<T> {
    protected SparseArray<FirebaseEntityListener<T>> listeners;
    private T t;

    public FirebaseObservableEntity() {
        t = (T) this;
    }

    public void notifyEntityChanged() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(listeners.keyAt(i)).onChanged(t);
        }

    }

    public void notifyEntityDeleted() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(listeners.keyAt(i)).onDeleted(t);
        }
    }

    public void addListener(int id, FirebaseEntityListener<T> listener) {
        listeners.put(id, listener);
    }

    public void removeListener(int id) {
        listeners.remove(id);
    }

    public interface FirebaseEntityListener<T> {
        void onChanged(T t);

        void onDeleted(T t);
    }
}

package com.example.portable.firebasetests.network;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Created by Black on 19.04.2017.
 */

public abstract class FirebaseEntity {
    private final ArrayList<FirebaseEntityListener> listeners = new ArrayList<>();
    protected String id;
    private boolean synced;

    public FirebaseEntity() {
    }

    public void notifyEntityChanged() {
        for (FirebaseEntityListener listener : listeners) {
            listener.onChanged();
        }

    }

    public void notifyEntityDeleted() {
        for (FirebaseEntityListener listener : listeners) {
            listener.onDeleted();
        }
    }

    public void addListener(FirebaseEntityListener listener) {
        listeners.add(listener);
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract void init(FirebaseEntity entity);

    public abstract boolean isIdentical(FirebaseEntity entity);

    public interface FirebaseEntityListener<T> {
        void onChanged();

        void onDeleted();
    }
}

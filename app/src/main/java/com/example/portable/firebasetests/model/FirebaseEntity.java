package com.example.portable.firebasetests.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by Black on 19.04.2017.
 */

public abstract class FirebaseEntity implements Serializable {
    protected String id;
    private boolean synced;

    public FirebaseEntity() {
    }

    @Exclude
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
}

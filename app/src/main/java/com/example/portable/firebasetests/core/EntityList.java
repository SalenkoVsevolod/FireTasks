package com.example.portable.firebasetests.core;


import android.util.Log;

import com.example.portable.firebasetests.network.FirebaseEntity;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Salenko Vsevolod on 20.04.2017.
 */

public class EntityList<T extends FirebaseEntity> extends ArrayList<T> {
    private final ArrayList<FirebaseObserver.OnEntityCreatedListener> createdListeners = new ArrayList<>();

    public T getById(String id) {
        for (T t : this) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public boolean containsId(String id) {
        for (T t : this) {
            if (t.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<FirebaseObserver.OnEntityCreatedListener> getCreatedListeners() {
        return createdListeners;
    }

    public void sync(EntityList<T> syncedArray) {
        Log.i("fireSync", "sync in array");
        for (T t : this) {
            t.setSynced(false);
        }
        for (T st : syncedArray) {
            if (this.containsId(st.getId())) {
                T t = this.getById(st.getId());
                if (!t.isIdentical(st)) {
                    t.init(st);
                    t.notifyEntityChanged();
                }
                t.setSynced(true);
            } else {
                st.setSynced(true);
                add(st);
                for (FirebaseObserver.OnEntityCreatedListener listener : createdListeners) {
                    listener.onCreated(st);
                }
            }
        }
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (!t.isSynced()) {
                t.notifyEntityDeleted();
                iterator.remove();
            }
        }
    }
}

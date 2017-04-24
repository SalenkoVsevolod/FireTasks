package com.example.portable.firebasetests.model;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Salenko Vsevolod on 20.04.2017.
 */

public class EntityList<T extends FirebaseEntity> extends ArrayList<T> {
    private final ArrayList<FirebaseEntityListener<T>> listeners = new ArrayList<>();

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

    public void subscribe(FirebaseEntityListener<T> listener) {
        listeners.add(listener);
    }

    public void unsubscribe(FirebaseEntityListener<T> listener) {
        listeners.remove(listener);
    }

    public void sync(EntityList<T> syncedArray) {
        for (T t : this) {
            t.setSynced(false);
        }
        for (T st : syncedArray) {
            if (this.containsId(st.getId())) {
                T t = this.getById(st.getId());
                if (!t.isIdentical(st)) {
                    t.init(st);
                    for (FirebaseEntityListener<T> listener : listeners) {
                        listener.onChanged(t);
                    }
                }
                t.setSynced(true);
            } else {
                st.setSynced(true);
                add(st);
                for (FirebaseEntityListener<T> listener : listeners) {
                    listener.onCreated(st);
                }
            }
        }
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (!t.isSynced()) {
                for (FirebaseEntityListener<T> listener : listeners) {
                    listener.onDeleted(t);
                }
                iterator.remove();
            }
        }
    }

    public interface FirebaseEntityListener<T> {
        void onChanged(T t);

        void onCreated(T t);

        void onDeleted(T t);
    }
}

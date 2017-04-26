package com.example.portable.firebasetests.network.listeners;

import com.example.portable.firebasetests.model.EntityList;
import com.example.portable.firebasetests.model.FirebaseEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Salenko Vsevolod on 20.04.2017.
 */

public abstract class FirebaseEntitySyncTask<T extends FirebaseEntity> implements Runnable {
    private ValueEventListener listener;
    private DatabaseReference dbReference;

    public FirebaseEntitySyncTask(DatabaseReference dbReference) {
        this.dbReference = dbReference;
    }

    @Override
    public void run() {
        addListener();
    }

    private void addListener() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onDataChanged(parseEntities(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbReference.addValueEventListener(listener);
    }

    protected abstract void onDataChanged(EntityList<T> entities);

    protected abstract EntityList<T> parseEntities(DataSnapshot dataSnapshot);
}

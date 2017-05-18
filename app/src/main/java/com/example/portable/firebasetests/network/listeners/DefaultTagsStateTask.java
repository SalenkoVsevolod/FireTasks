package com.example.portable.firebasetests.network.listeners;

import android.os.AsyncTask;

import com.example.portable.firebasetests.network.FirebaseReferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Black on 25.04.2017.
 */

public class DefaultTagsStateTask extends AsyncTask<Void, Void, Void> {
    private DefaultTagsCreatedListener mListener;

    public DefaultTagsStateTask(DefaultTagsCreatedListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        FirebaseReferenceManager.getInstance().getUserReference().child("defaultTagsCreated").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListener.created(dataSnapshot.getValue() != null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }

    public interface DefaultTagsCreatedListener {
        void created(boolean created);
    }
}

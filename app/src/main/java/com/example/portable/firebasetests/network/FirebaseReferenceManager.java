package com.example.portable.firebasetests.network;

import com.example.portable.firebasetests.core.Preferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class FirebaseReferenceManager {
    private static FirebaseReferenceManager instance;
    private DatabaseReference mUserReference;

    private FirebaseReferenceManager() {
        refresh();
    }

    public static FirebaseReferenceManager getInstance() {
        if (instance == null) {
            instance = new FirebaseReferenceManager();
        }
        return instance;
    }

    public void refresh() {
        mUserReference = FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId());
    }

    public DatabaseReference getUserReference() {
        return mUserReference;
    }

    public DatabaseReference getTagsReference() {
        return mUserReference.child("tags");
    }

    public DatabaseReference getDayReference(int day) {
        return mUserReference.child("days").child("" + day);
    }

    public DatabaseReference getTaskReference(int day, String id) {
        return getDayReference(day).child(id);
    }

    public DatabaseReference getRemindersReference() {
        return mUserReference.child("reminders");
    }
}

package com.example.portable.firebasetests.network;

import com.example.portable.firebasetests.core.Preferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class FirebaseReferenceManager {
    private static FirebaseReferenceManager instance;
    private DatabaseReference userReference;

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
        userReference = FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId());
    }

    public DatabaseReference getTagsReference() {
        return userReference.child("tags");
    }

    public DatabaseReference getDayReference(int day) {
        return userReference.child("days").child("" + day);
    }

    public DatabaseReference getTaskReference(int day, String id) {
        return getDayReference(day).child(id);
    }

    public DatabaseReference getRemindersReference() {
        return userReference.child("reminders");
    }
}

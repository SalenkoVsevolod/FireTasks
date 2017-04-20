package com.example.portable.firebasetests.network;

import com.example.portable.firebasetests.core.Preferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class FirebaseMediator {
    private DatabaseReference userReference;

    protected FirebaseMediator() {
        userReference = FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId());
    }

    public DatabaseReference getTagsReference() {
        return userReference.child("tags");
    }

    public DatabaseReference getDayReference(int day) {
        return userReference.child("days").child("" + day);
    }

    public DatabaseReference getRemindersReference() {
        return userReference.child("reminders");
    }
}

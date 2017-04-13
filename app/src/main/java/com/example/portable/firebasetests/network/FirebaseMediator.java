package com.example.portable.firebasetests.network;

import com.example.portable.firebasetests.core.Preferences;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class FirebaseMediator {
    public DatabaseReference getTaskReference(int day, String taskId) {
        return getDayReference(day).child(taskId);
    }

    public DatabaseReference getTagsReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("tags");
    }

    public DatabaseReference getDayReference(int day) {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("days").child("" + day);
    }

    public DatabaseReference getRemindersReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("reminders");
    }
}

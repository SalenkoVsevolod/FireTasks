package com.example.portable.firebasetests.network.listeners;

import android.os.AsyncTask;

import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 13.04.2017.
 */

public class TaskFirebaseListener extends AsyncTask<Void, Task, Void> {
    private OnTaskChangingListener listener;
    private int day;
    private String id;
    private ValueEventListener valueEventListener;

    public TaskFirebaseListener(int day, String id, OnTaskChangingListener listener) {
        this.listener = listener;
        this.day = day;
        this.id = id;
    }

    @Override
    protected Void doInBackground(Void... v) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> taskMap = (HashMap<String, Object>) dataSnapshot.getValue();
                listener.onChange(new Task(taskMap));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseUtils.getInstance().getTaskReference(day, id).addValueEventListener(valueEventListener);
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        FirebaseUtils.getInstance().getTaskReference(day, id).removeEventListener(valueEventListener);
    }

    public interface OnTaskChangingListener {
        void onChange(Task task);
    }
}

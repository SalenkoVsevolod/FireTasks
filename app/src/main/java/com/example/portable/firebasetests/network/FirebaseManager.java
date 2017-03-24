package com.example.portable.firebasetests.network;

import android.util.SparseArray;

import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by Black on 18.03.2017.
 */

public class FirebaseManager {
    private static FirebaseManager instance;
    private SparseArray<DataObserverTask> tasks;

    private FirebaseManager() {
        tasks = new SparseArray<>();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    DatabaseReference getWeekReference(int week) {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("" + week);
    }

    public void setWeekListener(int week, DataObserverTask.DataChangingListener listener) {
        DataObserverTask task = new DataObserverTask(week, listener);
        tasks.put(week, task);
        task.execute();
    }

    public void deleteTask(int week, String taskId) {
        FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("" + week).child(taskId).setValue(null);
    }

    public void removeWeekListener(int week) {
        tasks.get(week).cancel(true);
    }

    public void deleteSubtask(int week, String taskId, String subtaskId) {
        getTaskReference(week, taskId).child("subTasks").child(subtaskId).setValue(null);
    }

    public void saveTask(Task task) {
        int week = task.getCalendar().get(Calendar.WEEK_OF_YEAR);
        getTaskReference(week, task.getId()).setValue(task);
        for (Remind r : task.getReminds()) {
            getTaskReference(week, task.getId()).child("reminds").child(r.getId()).setValue(r);
        }
        for (SubTask subTask : task.getSubTasks()) {
            DatabaseReference ref = getTaskReference(week, task.getId()).child("subTasks")
                    .child(subTask.getId());
            ref.setValue(subTask);
        }

    }

    private DatabaseReference getTaskReference(int week, String taskId) {
        return getWeekReference(week).child(taskId);
    }

    public void setSubTaskDone(int week, String taskId, SubTask subTask) {
        getTaskReference(week, taskId).child("subTasks").child(subTask.getId()).child("done").setValue(subTask.isDone());
    }

    public void removeReminder(int week, String taskId, Remind remind) {
        getTaskReference(week, taskId).child("reminds").child(remind.getTimeStamp() + "").setValue(null);
    }
}

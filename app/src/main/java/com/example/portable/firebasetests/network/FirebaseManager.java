package com.example.portable.firebasetests.network;

import android.util.SparseArray;

import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Created by Black on 18.03.2017.
 */

public class FirebaseManager {
    private static FirebaseManager instance;
    private SparseArray<DayObserverTask> tasks;
    private TagsObserverTask tagsObserverTask;

    private FirebaseManager() {
        tasks = new SparseArray<>();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void setDayListener(int day, DayObserverTask.DataChangingListener listener) {
        DayObserverTask task = new DayObserverTask(day, listener);
        tasks.put(day, task);
        task.execute();
    }

    public void setTagsListener(TagsObserverTask.OnTagsSyncListener listener) {
        tagsObserverTask = new TagsObserverTask(listener);
        tagsObserverTask.execute();
    }

    public void removeTagsListener() {
        if (tagsObserverTask != null) {
            tagsObserverTask.cancel(true);
        }
    }

    public void setTagSingleListener(String tagId, TagSingleGetter.OnTagGetListener listener) {
        TagSingleGetter getter = new TagSingleGetter(tagId, listener);
        getter.execute();
    }

    public void deleteTask(int day, String taskId) {
        getTaskReference(day, taskId).setValue(null);
    }

    public void removeDayListener(int day) {
        tasks.get(day).cancel(true);
    }

    public void saveTask(Task task) {
        int day = task.getCalendar().get(Calendar.DAY_OF_YEAR);
        getTaskReference(day, task.getId()).setValue(task);
        for (Remind r : task.getReminds()) {
            getTaskReference(day, task.getId()).child("reminds").child(r.getId()).setValue(r);
        }
        for (SubTask subTask : task.getSubTasks()) {
            DatabaseReference ref = getTaskReference(day, task.getId()).child("subTasks")
                    .child(subTask.getId());
            ref.setValue(subTask);
        }

    }

    private DatabaseReference getTaskReference(int day, String taskId) {
        return getDayReference(day).child(taskId);
    }

    public void setSubTaskDone(int day, String taskId, SubTask subTask) {
        getTaskReference(day, taskId).child("subTasks").child(subTask.getId()).child("done").setValue(subTask.isDone());
    }

    public void removeReminder(int day, String taskId, Remind remind) {
        getTaskReference(day, taskId).child("reminds").child(remind.getId()).setValue(null);
    }

    public DatabaseReference getTagsReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("tags");
    }

    DatabaseReference getDayReference(int day) {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("days").child("" + day);
    }

    public void addTag(Tag tag) {
        getTagsReference().child(tag.getId()).setValue(tag);
    }
}

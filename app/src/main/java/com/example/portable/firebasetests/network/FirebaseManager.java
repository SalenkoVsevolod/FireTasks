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
    private SparseArray<WeekObserverTask> tasks;
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

    DatabaseReference getWeekReference(int week) {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("weeks").child("" + week);
    }

    public void setWeekListener(int week, WeekObserverTask.DataChangingListener listener) {
        WeekObserverTask task = new WeekObserverTask(week, listener);
        tasks.put(week, task);
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

    public void deleteTask(int week, String taskId) {
        getTaskReference(week, taskId).setValue(null);
    }

    public void removeWeekListener(int week) {
        tasks.get(week).cancel(true);
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
        getTaskReference(week, taskId).child("reminds").child(remind.getId()).setValue(null);
    }

    public DatabaseReference getTagsReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(Preferences.getInstance().readUserId()).child("tags");
    }

    public void addTag(Tag tag) {
        getTagsReference().child(tag.getId()).setValue(tag);
    }

    public void removeTag(Tag tag) {
        getTagsReference().child(tag.getId()).setValue(null);
    }
}

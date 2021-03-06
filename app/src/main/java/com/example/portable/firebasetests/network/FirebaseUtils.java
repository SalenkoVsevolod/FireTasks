package com.example.portable.firebasetests.network;

import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Black on 18.03.2017.
 */

public class FirebaseUtils {
    private static FirebaseUtils instance;

    public static FirebaseUtils getInstance() {
        if (instance == null) {
            instance = new FirebaseUtils();
        }
        return instance;
    }


    public void deleteTask(int day, String taskId) {
        FirebaseReferenceManager.getInstance().getDayReference(day).child(taskId).removeValue();
    }


    public void saveTask(Task task) {
        int day = task.getCalendar().get(Calendar.DAY_OF_YEAR);
        FirebaseReferenceManager.getInstance().getTaskReference(day, task.getId()).setValue(task);

        for (String id : task.getReminds()) {
            FirebaseReferenceManager.getInstance().getTaskReference(day, task.getId()).child("reminders").child(id).setValue(true);
        }

        for (SubTask subTask : task.getSubTasks()) {
            FirebaseReferenceManager.getInstance().getTaskReference(day, task.getId()).child("subTasks")
                    .child(subTask.getId())
                    .setValue(subTask);
        }
    }

    public void saveReminders(ArrayList<Remind> reminders) {
        for (Remind remind : reminders) {
            FirebaseReferenceManager.getInstance().getRemindersReference().child(remind.getId()).setValue(remind);
        }
    }

    public void setSubTaskDone(int day, String taskId, String subtaskId, boolean done) {
        FirebaseReferenceManager.getInstance().getTaskReference(day, taskId).child("subTasks").child(subtaskId).child("done").setValue(done);
    }

    public void removeReminder(int day, String taskId, String reminderId) {
        FirebaseReferenceManager.getInstance().getRemindersReference().child(reminderId).removeValue();
        FirebaseReferenceManager.getInstance().getTaskReference(day, taskId).child("reminders").child(reminderId).removeValue();
    }

    public void removeGlobalReminds(ArrayList<String> ids) {
        for (String s : ids) {
            FirebaseReferenceManager.getInstance().getRemindersReference().child(s).removeValue();
        }
    }

    public void saveTag(Tag tag) {
        FirebaseReferenceManager.getInstance().getTagsReference().child(tag.getId()).setValue(tag);
        if (tag.getTasks() != null) {
            for (int i = 0; i < tag.getTasks().size(); i++) {
                for (String id : tag.getTasks().get(tag.getTasks().keyAt(i))) {
                    FirebaseReferenceManager.getInstance().getTagsReference()
                            .child(tag.getId())
                            .child("tasks")
                            .child(tag.getTasks().keyAt(i) + "")
                            .child(id)
                            .setValue(true);
                }
            }
        }
    }

    public void deleteTag(Tag tag) {
        FirebaseReferenceManager.getInstance().getTagsReference().child(tag.getId()).removeValue();
    }

    public void deleteTasksFromTag(Tag tag) {
        for (int i = 0; i < tag.getTasks().size(); i++) {
            int day = tag.getTasks().keyAt(i);
            for (String id : tag.getTasks().get(day)) {
                deleteTask(day, id);
            }
        }
    }

    public void createDefaultTags() {
        for (Tag t : Tag.DEFAULT_TAGS) {
            saveTag(t);
        }
        setDefaultTagsCreated();
    }

    private void setDefaultTagsCreated() {
        FirebaseReferenceManager.getInstance().getUserReference().child("defaultTagsCreated").setValue(true);
    }

    public void selectTag(String oldTagId, String newTagId, int day, String taskId) {
        if (oldTagId != null) {
            FirebaseReferenceManager.getInstance().getTagsReference().child(oldTagId).child("tasks").child(day + "").child(taskId).removeValue();
        }
        FirebaseReferenceManager.getInstance().getTagsReference().child(newTagId).child("tasks").child(day + "").child(taskId).setValue(true);
    }

    public void setTaskDone(int day, String taskId, boolean done) {
        FirebaseReferenceManager.getInstance().getTaskReference(day, taskId).child("done").setValue(done);
    }
}

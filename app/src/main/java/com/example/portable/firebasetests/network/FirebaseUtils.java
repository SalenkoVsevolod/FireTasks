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

    public void setSubTaskDone(int day, String taskId, SubTask subTask) {
        //TODO     getTaskReference(day, taskId).child("subTasks").child(subTask.getId()).child("done").setValue(subTask.isDone());
    }

    public void removeReminder(int day, String taskId, String reminderId) {
        FirebaseReferenceManager.getInstance().getRemindersReference().child(reminderId).removeValue();
        FirebaseReferenceManager.getInstance().getTaskReference(day, taskId).child("remindera").child(reminderId).removeValue();
    }


    public void addTag(Tag tag) {
        FirebaseReferenceManager.getInstance().getTagsReference().child(tag.getId()).setValue(tag);
    }
}

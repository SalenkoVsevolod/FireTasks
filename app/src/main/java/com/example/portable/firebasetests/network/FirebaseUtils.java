package com.example.portable.firebasetests.network;

import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;

import java.util.Calendar;

/**
 * Created by Black on 18.03.2017.
 */

public class FirebaseUtils extends FirebaseMediator {
    private static FirebaseUtils instance;

    public static FirebaseUtils getInstance() {
        if (instance == null) {
            instance = new FirebaseUtils();
        }
        return instance;
    }


    public void deleteTask(int day, String taskId) {
        //TODO getTaskReference(day, taskId).setValue(null);
    }


    public void saveTask(Task task) {
        int day = task.getCalendar().get(Calendar.DAY_OF_YEAR);
   /* TODO     getTaskReference(day, task.getId()).setValue(task);
        for (Remind r : task.getReminds()) {
            getTaskReference(day, task.getId()).child("reminds").child(r.getId()).setValue(r);
        }
        for (SubTask subTask : task.getSubTasks()) {
            DatabaseReference ref = getTaskReference(day, task.getId()).child("subTasks")
                    .child(subTask.getId());
            ref.setValue(subTask);
        }
*/
    }


    public void setSubTaskDone(int day, String taskId, SubTask subTask) {
        //TODO     getTaskReference(day, taskId).child("subTasks").child(subTask.getId()).child("done").setValue(subTask.isDone());
    }

    public void removeReminder(int day, String taskId, Remind remind) {
        //TODO   getTaskReference(day, taskId).child("reminds").child(remind.getId()).setValue(null);
    }


    public void addTag(Tag tag) {
        getTagsReference().child(tag.getId()).setValue(tag);
    }
}

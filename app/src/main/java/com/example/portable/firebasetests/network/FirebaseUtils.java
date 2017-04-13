package com.example.portable.firebasetests.network;

import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.google.firebase.database.DatabaseReference;

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


    public void deleteTask(Task task) {
        getTaskReference(task.getDayOfYear(), task.getId()).removeValue();
        for (Remind r : task.getReminds()) {
            getTagsReference().child(r.getId()).removeValue();
        }
    }

    //TODO crutch, do not create reference each time
    public void saveTask(Task task) {
        getTaskReference(task.getDayOfYear(), task.getId()).setValue(task);
        for (Remind r : task.getReminds()) {
            getTaskReference(task.getDayOfYear(), task.getId()).child("reminds").child(r.getId()).setValue(r);
            getRemindersReference().child(r.getId()).setValue(r);
        }
        for (SubTask subTask : task.getSubTasks()) {
            DatabaseReference ref = getTaskReference(task.getDayOfYear(), task.getId()).child("subTasks")
                    .child(subTask.getId());
            ref.setValue(subTask);
        }
    }


    public void setSubTaskDone(int day, String id, SubTask subTask) {
        getTaskReference(day, id).child("subTasks").child(subTask.getId()).child("done").setValue(subTask.isDone());
    }

    public void removeReminder(Task task, Remind remind) {
        getRemindersReference().child(remind.getId()).removeValue();
        getTaskReference(task.getDayOfYear(), task.getId()).child("reminds").child(remind.getId()).removeValue();
    }

    public void addTag(Tag tag) {
        getTagsReference().child(tag.getId()).setValue(tag);
    }

    public void selectTag(Task task, Tag tag) {
        getTagsReference().child(tag.getId())
                .child("tasks").child(task.getDayOfYear() + "")
                .child(task.getId())
                .setValue(true);
        getTaskReference(task.getDayOfYear(), task.getId()).child("tag").child(tag.getId()).setValue(tag);
    }
}

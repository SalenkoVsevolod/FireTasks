package com.example.portable.firebasetests.network.listeners;

import com.example.portable.firebasetests.core.EntityList;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Salenko Vsevolod on 20.04.2017.
 */

public class TaskDaySyncTask extends FirebaseEntitySyncTask<Task> {
    private int currentYear, day;

    public TaskDaySyncTask(int day) {
        super(FirebaseUtils.getInstance().getDayReference(day));
        this.day = day;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentYear = calendar.get(Calendar.YEAR);
    }

    @Override
    protected void onDataChanged(EntityList<Task> entities) {

    }

    @Override
    protected EntityList<Task> parseEntities(DataSnapshot dataSnapshot) {
        HashMap<String, Object> tasks = (HashMap<String, Object>) dataSnapshot.getValue();
        if (tasks == null) {
            tasks = new HashMap<>();
        }
        EntityList<Task> res = new EntityList<>();
        Set<String> keys = tasks.keySet();
        for (String key : keys) {
            Task task = new Task((HashMap<String, Object>) tasks.get(key));
            task.setId(key);
            Notifier.setAlarms(task);
            res.add(task);
        }
        return res;
    }

    private void deleteOutdatedTasks(ArrayList<Task> tasks) {
        for (Task t : tasks) {
            if (t.getCalendar().get(Calendar.YEAR) < currentYear) {
                FirebaseUtils.getInstance().deleteTask(day, t.getId());
            }
        }
    }
}
package com.example.portable.firebasetests.network.listeners;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Portable on 25.01.2017.
 */
@SuppressWarnings("unchecked")
public class DayFirebaseListener extends AsyncTask<Void, ArrayList<Task>, Void> {
    private DataChangingListener dataChangingListener;
    private ValueEventListener listener;
    private int day;
    private int currentYear;

    public DayFirebaseListener(int week, DataChangingListener listener) {
        this.day = week;
        dataChangingListener = listener;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentYear = calendar.get(Calendar.YEAR);
    }

    @Override
    protected Void doInBackground(Void... params) {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, Object> tasks = (HashMap<String, Object>) dataSnapshot.getValue();
                if (tasks == null) {
                    tasks = new HashMap<>();
                }
                ArrayList<Task> taskArrayList = getTasks(tasks);
                for (Task t : taskArrayList) {
                    if (t.getCalendar().get(Calendar.YEAR) < currentYear) {
                        FirebaseUtils.getInstance().deleteTask(day, t.getId());
                    }
                }
                publishProgress(taskArrayList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(FireTasksApp.getInstance(), "connection cancelled", Toast.LENGTH_LONG).show();
            }
        };
        FirebaseUtils.getInstance().getDayReference(day).addValueEventListener(listener);
        return null;
    }

    private ArrayList<Task> getTasks(HashMap<String, Object> map) {
        ArrayList<Task> res = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Task task = new Task((HashMap<String, Object>) map.get(key));
            task.setId(key);
            Notifier.setAlarms(task);
            res.add(task);
        }
        return res;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        if (listener != null) {
            FirebaseListenersManager.getInstance().getDayReference(day).removeEventListener(listener);
        }
    }

    @Override
    protected void onProgressUpdate(ArrayList<Task>... values) {
        super.onProgressUpdate(values);
        dataChangingListener.onDataChanged(values[0]);
    }

    public interface DataChangingListener {
        void onDataChanged(ArrayList<Task> tasks);
    }
}

package com.example.portable.firebasetests.async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.notifications.Notifier;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Portable on 25.01.2017.
 */
@SuppressWarnings("unchecked")
public class DataObserverTask extends AsyncTask<Void, ArrayList<Task>, Void> {
    private DataChangingListener dataChangingListener;
    private String id;
    private Context context;
    private DatabaseReference myRef;
    private ValueEventListener listener;
    private int week;
    private int currentYear;

    public DataObserverTask(Context context, String id, int week) {
        this.id = id;
        this.context = context;
        this.week = week;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        currentYear = calendar.get(Calendar.YEAR);
    }

    @Override
    protected Void doInBackground(Void... params) {
        myRef = FirebaseDatabase.getInstance().getReference("users").child(id).child("" + week);
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
                        deleteTask(t.getId());
                    }
                }
                publishProgress(taskArrayList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "connection cancelled", Toast.LENGTH_LONG).show();
            }
        };
        myRef.addValueEventListener(listener);
        return null;
    }

    private ArrayList<Task> getTasks(HashMap<String, Object> map) {
        ArrayList<Task> res = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Task task = new Task((HashMap<String, Object>) map.get(key));
            task.setId(key);
            if (task.isTimeSpecified() && task.getTimeStamp() > System.currentTimeMillis()) {
                Notifier.setAlarm(task, context);
            }
            res.add(task);

        }
        return res;
    }


    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        myRef.removeEventListener(listener);
    }

    @Override
    protected void onProgressUpdate(ArrayList<Task>... values) {
        super.onProgressUpdate(values);
        dataChangingListener.onDataChanged(values[0]);
    }

    public void setDataChangingListener(DataChangingListener dataChangingListener) {
        this.dataChangingListener = dataChangingListener;
    }

    private void deleteTask(String taskId) {
        myRef = FirebaseDatabase.getInstance().getReference("users").child(id).child("" + week).child(taskId);
        myRef.setValue(null);
    }
    public interface DataChangingListener {
        void onDataChanged(ArrayList<Task> tasks);
    }
}

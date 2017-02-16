package com.example.portable.firebasetests.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.example.portable.firebasetests.MySharedPreferences;
import com.example.portable.firebasetests.Notifier;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TimeUtils;
import com.example.portable.firebasetests.activities.TaskCreateActivity;
import com.example.portable.firebasetests.adapters.TasksExpandableAdapter;
import com.example.portable.firebasetests.listeners.DataChangingListener;
import com.example.portable.firebasetests.listeners.OnDateIdentifiedListener;
import com.example.portable.firebasetests.listeners.OnTaskClickListener;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.model.TasksDay;
import com.example.portable.firebasetests.tasks.DataObserverTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.R.string.cancel;
import static android.R.string.ok;

public class TasksWeekFragment extends Fragment {
    private static final String WEEK_ARG = "week";
    private String userId;
    private ProgressBar progressBar;
    private DataObserverTask dataObserverTask;
    private RecyclerView expandableListView;
    private TasksExpandableAdapter tasksExpandableAdapter;
    private int weekOfYear;

    public TasksWeekFragment() {
    }

    public static TasksWeekFragment newInstance(int weekOfYear) {
        TasksWeekFragment fragment = new TasksWeekFragment();
        Bundle args = new Bundle();
        args.putInt(WEEK_ARG, weekOfYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            weekOfYear = getArguments().getInt(WEEK_ARG);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tasks_list, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.tasksListProgressBar);
        expandableListView = (RecyclerView) rootView.findViewById(R.id.tasksExpandableListView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        userId = MySharedPreferences.readUserId(getActivity());
        dataObserverTask = new DataObserverTask(getActivity(), userId, weekOfYear);
        dataObserverTask.setDataChangingListener(new DataChangingListener() {
            @Override
            public void onDataChanged(ArrayList<Task> tasks) {
                if (tasks == null) {
                    tasks = new ArrayList<>();
                }
                showList();
                tasksExpandableAdapter = new TasksExpandableAdapter(getActivity(), generateWeekFromTasks(tasks));
                tasksExpandableAdapter.setOnGroupClickListener(new OnDateIdentifiedListener() {
                    @Override
                    public void onIdentified(int year, int weekOfYear, int dayOfWeek) {
                        openTaskCreateFragment(year, weekOfYear, dayOfWeek);
                    }
                });
                tasksExpandableAdapter.setOnTaskLongClickListener(new OnTaskClickListener() {
                    @Override
                    public void onClick(Task task) {
                        deleteDialog(task);
                    }
                });
                tasksExpandableAdapter.setOnTaskClickListener(new OnTaskClickListener() {
                    @Override
                    public void onClick(Task task) {
                        openTaskCreateFragment(task);
                    }
                });
                expandableListView.setLayoutManager(new LinearLayoutManager(getActivity()));
                expandableListView.setAdapter(tasksExpandableAdapter);
            }
        });
        dataObserverTask.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dataObserverTask != null) {
            dataObserverTask.cancel(true);
        }
    }

    private void openTaskCreateFragment(Task task) {
        Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
        intent.putExtra(TaskCreateActivity.TASK_ARG, task);
        startActivity(intent);
    }

    private void openTaskCreateFragment(int year, int week, int day) {
        Task task = new Task();
        task.getCalendar().set(Calendar.YEAR, year);
        task.getCalendar().set(Calendar.WEEK_OF_YEAR, week);
        task.getCalendar().roll(Calendar.DAY_OF_WEEK, TimeUtils.adapterToReal(day));
        Log.i("creating", "open task creator with week:" + task.getCalendar().get(Calendar.WEEK_OF_YEAR));
        openTaskCreateFragment(task);
    }

    private void showList() {
        expandableListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void deleteDialog(final Task task) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Delete task \"" + task.getDescription() + "\"?");
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Notifier.removeAlarm(getActivity(), (int) task.getTimeStamp());
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users").child(userId).child(weekOfYear + "").child(task.getId());
                myRef.setValue(null);
            }
        });
        builder.setNegativeButton(cancel, null);
        builder.setCancelable(true);
        builder.show();
    }

    private SparseArray<TasksDay> getCurrentWeek() {
        SparseArray<TasksDay> parentObjects = new SparseArray<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        for (int i = 1; i < 8; i++) {
            TasksDay day = new TasksDay(calendar.get(Calendar.YEAR), weekOfYear, i);
            parentObjects.put(TimeUtils.realToAdapter(day.getDay()), day);
        }
        return parentObjects;
    }

    private List<ParentObject> generateWeekFromTasks(ArrayList<Task> tasks) {
        SparseArray<TasksDay> week = getCurrentWeek();
        ArrayList<ParentObject> res = new ArrayList<>();
        for (Task t : tasks) {
            week.get(TimeUtils.realToAdapter(t.getCalendar().get(Calendar.DAY_OF_WEEK))).addChild(t);
        }
        for (int i = 0; i < week.size(); i++) {
            res.add(week.get(week.keyAt(i)));
        }
        return res;
    }

}
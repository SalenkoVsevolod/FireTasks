package com.example.portable.firebasetests.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.model.TasksDay;
import com.example.portable.firebasetests.network.DataObserverTask;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.ui.activities.TaskCreateActivity;
import com.example.portable.firebasetests.ui.adapters.TasksExpandableAdapter;
import com.example.portable.firebasetests.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.R.string.cancel;
import static android.R.string.ok;

public class TasksWeekFragment extends Fragment {
    private static final String WEEK_ARG = "week";
    private ProgressBar progressBar;
    private RecyclerView expandableListView;
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
        expandableListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    private TasksExpandableAdapter initExpandableListViewAdapter(List<ParentObject> parents) {

        TasksExpandableAdapter tasksExpandableAdapter = new TasksExpandableAdapter(getActivity(), parents);
        tasksExpandableAdapter.setOnGroupClickListener(new OnDateIdentifiedListener() {
            @Override
            public void onIdentified(int year, int weekOfYear, int dayOfWeek) {
                openTaskCreator(year, weekOfYear, dayOfWeek);
            }
        });
        tasksExpandableAdapter.setOnTaskLongClickListener(new TasksExpandableAdapter.OnTaskClickListener() {
            @Override
            public void onClick(Task task) {
                deleteDialog(task);
            }
        });
        tasksExpandableAdapter.setOnTaskClickListener(new TasksExpandableAdapter.OnTaskClickListener() {
            @Override
            public void onClick(Task task) {
                TaskCreateActivity.start(getActivity(), task);
            }
        });
        return tasksExpandableAdapter;
    }

    @Override
    public void onStart() {
        super.onStart();
        DataObserverTask.DataChangingListener listener = new DataObserverTask.DataChangingListener() {

            @Override
            public void onDataChanged(ArrayList<Task> tasks) {
                if (tasks == null) {
                    tasks = new ArrayList<>();
                }
                expandableListView.setAdapter(initExpandableListViewAdapter(generateWeekFromTasks(tasks)));
                showList();

            }
        };
        FirebaseManager.getInstance().setWeekListener(weekOfYear, listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseManager.getInstance().removeWeekListener(weekOfYear);
    }


    private void openTaskCreator(int year, int week, int day) {
        Task task = new Task();
        task.getCalendar().set(Calendar.DAY_OF_WEEK, day);
        task.getCalendar().set(Calendar.WEEK_OF_YEAR, week);
        task.getCalendar().set(Calendar.YEAR, year);
        TaskCreateActivity.start(getActivity(), task);
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
                Notifier.removeAlarm((int) task.getTimeStamp());
                FirebaseManager.getInstance().deleteTask(weekOfYear, task.getId());
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

    public interface OnDateIdentifiedListener {
        void onIdentified(int year, int weekOfYear, int dayOfWeek);
    }
}
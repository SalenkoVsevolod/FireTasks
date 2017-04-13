package com.example.portable.firebasetests.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.network.listeners.DayFirebaseListener;
import com.example.portable.firebasetests.ui.activities.TaskDisplayActivity;
import com.example.portable.firebasetests.ui.adapters.TasksDayRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DayFragment extends Fragment {
    private static final String DAY_OF_YEAR = "dayOfYear", SORTING = "sort";
    private RecyclerView tasksRecycler;
    private ProgressBar progressBar;
    private int dayOfYear;
    private ArrayList<Task> tasks;
    private String sortingTagId;
    private TextView deletingTextView;
    private TasksDayRecyclerAdapter adapter;
    private TextView noTasksTextView;

    public DayFragment() {
        // Required empty public constructor
    }

    //TODO looks like crutch
    public static DayFragment newInstance(int dayOfYear) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_OF_YEAR, dayOfYear);
        fragment.setArguments(args);
        return fragment;
    }

    public static DayFragment newInstance(int dayOfYear, String sortingTagId) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_OF_YEAR, dayOfYear);
        args.putString(SORTING, sortingTagId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayOfYear = getArguments().getInt(DAY_OF_YEAR);
            sortingTagId = getArguments().getString(SORTING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);
        tasksRecycler = (RecyclerView) rootView.findViewById(R.id.day_tasks_rv);
        noTasksTextView = (TextView) rootView.findViewById(R.id.no_tasks_tv);
        deletingTextView = (TextView) rootView.findViewById(R.id.deleting_tv);
        deletingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Task task : adapter.getTasksForDeleting()) {
                    FirebaseUtils.getInstance().deleteTask(task);
                }
                hideDeleting();
            }
        });
        progressBar = (ProgressBar) rootView.findViewById(R.id.day_progress_bar);
        tasks = new ArrayList<>();
        TasksDayRecyclerAdapter.OnTaskClickListener onTaskClickListener = new TasksDayRecyclerAdapter.OnTaskClickListener() {
            @Override
            public void onClick(Task task) {
                TaskDisplayActivity.start(getActivity(), task.getDayOfYear(), task.getId());
            }
        };
        TasksDayRecyclerAdapter.OnDeletingListener onDeletingListener = new TasksDayRecyclerAdapter.OnDeletingListener() {
            @Override
            public void onDeletingDisplay(boolean displaying) {
                if (displaying) {
                    showDeleting();
                } else {
                    deletingTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDeletingTasksNumberChanged(int number) {
                deletingTextView.setText(String.format(getString(R.string.delete_tasks), number));
            }
        };
        adapter = new TasksDayRecyclerAdapter(tasks, onTaskClickListener, onDeletingListener);
        tasksRecycler.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseListenersManager.getInstance().setDayListener(dayOfYear, new DayFirebaseListener.DataChangingListener() {
            @Override
            public void onDataChanged(ArrayList<Task> tasksArray) {
                tasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                progressBar.setVisibility(View.GONE);
                if (tasksArray.size() > 0) {
                    noTasksTextView.setVisibility(View.GONE);
                    tasksRecycler.setVisibility(View.VISIBLE);
                } else {
                    noTasksTextView.setVisibility(View.VISIBLE);
                }
                tasks.clear();
                tasks.addAll(tasksArray);
                if (sortingTagId != null) {
                    sortTasks();
                }
                tasksRecycler.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void setSortingTagIdAndSort(String tagId) {
        if (tagId != null) {
            sortingTagId = tagId;
            sortTasks();
        }
    }

    public void sortTasks() {
        if (tasks != null) {
            Collections.sort(tasks, new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    if (o1.getTag().getId().equals(sortingTagId)) {
                        return -1;
                    } else if (o2.getTag().getId().equals(sortingTagId)) {
                        return 1;
                    }
                    return 0;
                }
            });
            tasksRecycler.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseListenersManager.getInstance().removeDayListener(dayOfYear);
    }

    public boolean hideDeleting() {
        deletingTextView.setVisibility(View.GONE);
        return adapter.handleBackPress();
    }

    private void showDeleting() {
        deletingTextView.setVisibility(View.VISIBLE);
    }
}

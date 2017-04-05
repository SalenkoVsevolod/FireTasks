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

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.listeners.DayFirebaseListener;
import com.example.portable.firebasetests.ui.activities.TaskDisplayActivity;
import com.example.portable.firebasetests.ui.adapters.TasksDayRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DayFragment extends Fragment {
    private static final String DAY_OF_YEAR = "dayOfYear";
    private RecyclerView tasksRecycler;
    private ProgressBar progressBar;
    private int dayOfYear;
    private ArrayList<Task> tasks;
    private String sortingTagId;

    public DayFragment() {
        // Required empty public constructor
    }


    public static DayFragment newInstance(int dayOfYear) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(DAY_OF_YEAR, dayOfYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dayOfYear = getArguments().getInt(DAY_OF_YEAR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day, container, false);
        tasksRecycler = (RecyclerView) rootView.findViewById(R.id.day_tasks_rv);
        progressBar = (ProgressBar) rootView.findViewById(R.id.day_progress_bar);
        tasks = new ArrayList<>();
        tasksRecycler.setAdapter(new TasksDayRecyclerAdapter(tasks, new TasksDayRecyclerAdapter.OnTaskClickListener() {
            @Override
            public void onClick(Task task) {
                TaskDisplayActivity.start(getActivity(), task);
            }
        }));
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
                tasks.clear();
                tasks.addAll(tasksArray);
                if (sortingTagId != null) {
                    sortTasks();
                }
                tasksRecycler.getAdapter().notifyDataSetChanged();
                tasksRecycler.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setSortingTagIdAndSort(String tagId) {
        sortingTagId = tagId;
        sortTasks();
    }

    public void sortTasks() {
        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1.getTagId().equals(sortingTagId)) {
                    return -1;
                } else if (o2.getTagId().equals(sortingTagId)) {
                    return 1;
                }
                return 0;
            }
        });
        tasksRecycler.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseListenersManager.getInstance().removeDayListener(dayOfYear);
    }
}

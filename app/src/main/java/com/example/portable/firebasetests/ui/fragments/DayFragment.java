package com.example.portable.firebasetests.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.DayObserverTask;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.ui.adapters.TasksDayRecyclerAdapter;

import java.util.ArrayList;

public class DayFragment extends Fragment {
    private static final String DAY_OF_YEAR = "dayOfYear";
    private RecyclerView tasksRecycler;
    private ProgressBar progressBar;
    private ImageView moreImageView;
    private int dayOfYear;


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
        moreImageView = (ImageView) rootView.findViewById(R.id.more_dots_tv);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseManager.getInstance().setDayListener(dayOfYear, new DayObserverTask.DataChangingListener() {
            @Override
            public void onDataChanged(ArrayList<Task> tasks) {
                tasksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                progressBar.setVisibility(View.GONE);
                tasksRecycler.setAdapter(new TasksDayRecyclerAdapter(tasks));
                tasksRecycler.setVisibility(View.VISIBLE);
            }
        });
    }
}

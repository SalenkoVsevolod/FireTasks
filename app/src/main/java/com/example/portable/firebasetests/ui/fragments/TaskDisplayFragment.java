package com.example.portable.firebasetests.ui.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.model.Task;

public class TaskDisplayFragment extends Fragment {
    private TextView description, tag, time;
    private static final String TASK_ARG = "task";
    private Task task;

    public TaskDisplayFragment() {
    }


    public static TaskDisplayFragment newInstance(Task task) {
        TaskDisplayFragment fragment = new TaskDisplayFragment();
        Bundle args = new Bundle();
        args.putSerializable(TASK_ARG, task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = (Task) getArguments().getSerializable(TASK_ARG);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        description.setText(task.getDescription());
        time.setText(task.getTimeString());
        tag.setText(TagsColors.getTags().get((int) task.getTagIndex()).getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_display, container, false);
        description = (TextView) rootView.findViewById(R.id.description_display);
        time = (TextView) rootView.findViewById(R.id.timeTextView);
        tag = (TextView) rootView.findViewById(R.id.tag_display);
        return rootView;
    }

}

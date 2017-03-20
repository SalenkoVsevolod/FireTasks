package com.example.portable.firebasetests.ui.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.ui.adapters.SubTaskAdapter;

import java.util.Calendar;

public class TaskDisplayFragment extends Fragment {
    public static final String TASK_DISPLAY_TAG = "display";
    private TextView description, tag, time;
    private static final String TASK_ARG = "task";
    private CardView tagCardView;
    private Task task;
    private RecyclerView subtasksRecyclerView;

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
        tagCardView.setCardBackgroundColor(TagsColors.getTagColor((int) task.getTagIndex()));
        SubTaskAdapter adapter = new SubTaskAdapter(task.getSubTasks(), new SubTaskAdapter.OnSubTaskCheckBoxCliCkListener() {
            @Override
            public void onClick(SubTask subTask, boolean checked) {
                subTask.setDone(checked);
                FirebaseManager.getInstance().setSubTaskDone(task.getCalendar().get(Calendar.WEEK_OF_YEAR), task.getId(), subTask);
            }
        });
        subtasksRecyclerView.setAdapter(adapter);
        subtasksRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_display, container, false);
        description = (TextView) rootView.findViewById(R.id.description_display);
        subtasksRecyclerView = (RecyclerView) rootView.findViewById(R.id.subTasksRecyclerView);
        time = (TextView) rootView.findViewById(R.id.timeTextView);
        tag = (TextView) rootView.findViewById(R.id.tag_display);
        tagCardView = (CardView) rootView.findViewById(R.id.tag_cardview);
        return rootView;
    }

}

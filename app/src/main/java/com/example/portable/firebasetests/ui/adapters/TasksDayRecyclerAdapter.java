package com.example.portable.firebasetests.ui.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.network.TagSingleGetter;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 04.04.2017.
 */

public class TasksDayRecyclerAdapter extends RecyclerView.Adapter<TasksDayRecyclerAdapter.DayTaskViewHolder> {
    private static final int MAX_SUBTASKS_DISPLAYING = 3;
    private ArrayList<Task> tasks;

    public TasksDayRecyclerAdapter(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public DayTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DayTaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final DayTaskViewHolder holder, int position) {
        holder.nameTextView.setText(tasks.get(position).getName());
        FirebaseManager.getInstance().setTagSingleListener(tasks.get(position).getTagId(), new TagSingleGetter.OnTagGetListener() {
            @Override
            public void onGet(Tag tag) {
                holder.tagTextView.setVisibility(View.VISIBLE);
                holder.tagTextView.setText(tag.getName());
                holder.tagTextView.setTextColor((int) tag.getColor());
            }
        });
        holder.subtasksRecycler.setLayoutManager(new LinearLayoutManager(FireTasksApp.getInstance()));
        SubTaskPreviewAdapter subTaskPreviewAdapter;
        if (tasks.get(position).getSubTasks().size() > MAX_SUBTASKS_DISPLAYING) {
            ArrayList<SubTask> newSubTasks = new ArrayList<>();
            for (int i = 0; i < MAX_SUBTASKS_DISPLAYING; i++) {
                newSubTasks.add(tasks.get(position).getSubTasks().get(i));
            }
            holder.moreDots.setVisibility(View.VISIBLE);
            subTaskPreviewAdapter = new SubTaskPreviewAdapter(newSubTasks);
        } else {
            holder.moreDots.setVisibility(View.GONE);
            subTaskPreviewAdapter = new SubTaskPreviewAdapter(tasks.get(position).getSubTasks());
        }
        holder.subtasksRecycler.setAdapter(subTaskPreviewAdapter);
        holder.progressBar.setProgress(tasks.get(position).getProgress());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class DayTaskViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView, nameTextView;
        RecyclerView subtasksRecycler;
        ImageView moreDots;
        ProgressBar progressBar;

        DayTaskViewHolder(View itemView) {
            super(itemView);
            tagTextView = (TextView) itemView.findViewById(R.id.tag_tv);
            nameTextView = (TextView) itemView.findViewById(R.id.task_name_tv);
            subtasksRecycler = (RecyclerView) itemView.findViewById(R.id.subTasksRecyclerView);
            moreDots = (ImageView) itemView.findViewById(R.id.more_dots_tv);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}

package com.example.portable.firebasetests.ui.adapters;

import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
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
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.listeners.TagFirebaseListener;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 04.04.2017.
 */

public class TasksDayRecyclerAdapter extends RecyclerView.Adapter<TasksDayRecyclerAdapter.DayTaskViewHolder> {
    private static final int MAX_SUBTASKS_DISPLAYING = 3;
    private ArrayList<Task> tasks;
    private OnTaskClickListener onTaskClicklistener;
    private View.OnLongClickListener longClickListener;

    public TasksDayRecyclerAdapter(ArrayList<Task> tasks, OnTaskClickListener onTaskClicklistener, View.OnLongClickListener longClickListener) {
        this.tasks = tasks;
        this.onTaskClicklistener = onTaskClicklistener;
        this.longClickListener = longClickListener;
    }

    @Override
    public DayTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DayTaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(final DayTaskViewHolder holder, int position) {
        final Task t = tasks.get(position);
        holder.nameTextView.setText(t.getName());
        View.OnClickListener onSubtaskClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTaskClicklistener.onClick(t);
            }
        };
        FirebaseListenersManager.getInstance().setTagListener(t.getTagId(), new TagFirebaseListener.OnTagGetListener() {
            @Override
            public void onGet(Tag tag) {
                holder.tagTextView.setText(tag.getName());
                holder.tagTextView.setTextColor((int) tag.getColor());
                Drawable drawable = holder.progressBar.getProgressDrawable();
                drawable.setColorFilter(new LightingColorFilter(0xFF000000, (int) tag.getColor()));
            }
        });
        holder.subtasksRecycler.setLayoutManager(new LinearLayoutManager(FireTasksApp.getInstance()));
        SubTaskPreviewAdapter subTaskPreviewAdapter;
        if (t.getSubTasks().size() > MAX_SUBTASKS_DISPLAYING) {
            ArrayList<SubTask> newSubTasks = new ArrayList<>();
            for (int i = 0; i < MAX_SUBTASKS_DISPLAYING; i++) {
                newSubTasks.add(t.getSubTasks().get(i));
            }
            holder.moreDots.setVisibility(View.VISIBLE);
            subTaskPreviewAdapter = new SubTaskPreviewAdapter(newSubTasks, onSubtaskClick, longClickListener);
        } else {
            holder.moreDots.setVisibility(View.GONE);
            subTaskPreviewAdapter = new SubTaskPreviewAdapter(t.getSubTasks(), onSubtaskClick, longClickListener);
        }
        holder.subtasksRecycler.setAdapter(subTaskPreviewAdapter);
        holder.progressBar.setProgress(t.getProgress());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface OnTaskClickListener {
        void onClick(Task task);
    }

    class DayTaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(longClickListener);
        }

        @Override
        public void onClick(View v) {
            onTaskClicklistener.onClick(tasks.get(getAdapterPosition()));
        }
    }
}

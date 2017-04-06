package com.example.portable.firebasetests.ui.adapters;

import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    private OnTaskInteractionListener onTaskInteractionListener;
    private boolean deleting = false;
    private int longClicked;

    public TasksDayRecyclerAdapter(ArrayList<Task> tasks, OnTaskInteractionListener onTaskInteractionListener) {
        this.tasks = tasks;
        this.onTaskInteractionListener = onTaskInteractionListener;
    }

    @Override
    public DayTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DayTaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false));
    }

    @Override
    public void onBindViewHolder(final DayTaskViewHolder holder, final int position) {

        final Task t = tasks.get(position);

        View.OnClickListener onSubtaskClick;
        View.OnLongClickListener onSubtaskLongClick;

        if (deleting) {
            holder.deletingCheckbox.setVisibility(View.VISIBLE);
            holder.deletingCheckbox.setChecked(longClicked == position);
            onSubtaskClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleting = false;
                    notifyDataSetChanged();
                }
            };
            onSubtaskLongClick = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deleting = false;
                    notifyDataSetChanged();
                    return true;
                }
            };
        } else {
            holder.deletingCheckbox.setVisibility(View.GONE);
            holder.deletingCheckbox.setChecked(false);
            onSubtaskClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskInteractionListener.onClick(t);
                }
            };
            onSubtaskLongClick = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.deletingCheckbox.setChecked(true);
                    setDeleting(true);
                    longClicked = position;
                    notifyDataSetChanged();
                    return true;
                }
            };
        }
        holder.nameTextView.setText(t.getName());

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
            subTaskPreviewAdapter = new SubTaskPreviewAdapter(newSubTasks, onSubtaskClick, onSubtaskLongClick);
        } else {
            holder.moreDots.setVisibility(View.GONE);
            subTaskPreviewAdapter = new SubTaskPreviewAdapter(t.getSubTasks(), onSubtaskClick, onSubtaskLongClick);
        }
        holder.subtasksRecycler.setAdapter(subTaskPreviewAdapter);
        holder.progressBar.setProgress(t.getProgress());
        holder.rootView.setOnClickListener(onSubtaskClick);
        holder.rootView.setOnLongClickListener(onSubtaskLongClick);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public interface OnTaskInteractionListener {
        void onClick(Task task);
    }

    class DayTaskViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView, nameTextView;
        RecyclerView subtasksRecycler;
        ImageView moreDots;
        ProgressBar progressBar;
        CheckBox deletingCheckbox;
        View rootView;

        DayTaskViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            tagTextView = (TextView) itemView.findViewById(R.id.tag_tv);
            deletingCheckbox = (CheckBox) itemView.findViewById(R.id.deleting_checkbox);
            nameTextView = (TextView) itemView.findViewById(R.id.task_name_tv);
            subtasksRecycler = (RecyclerView) itemView.findViewById(R.id.subTasksRecyclerView);
            moreDots = (ImageView) itemView.findViewById(R.id.more_dots_tv);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}

package com.example.portable.firebasetests.ui.adapters;

import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private OnTaskClickListener onTaskClickListener;
    private boolean deleting = false;
    private int longClicked;
    private OnDeletingListener onDeletingListener;
    private ArrayList<String> tasksForDeleting;

    public TasksDayRecyclerAdapter(ArrayList<Task> tasks, OnTaskClickListener onTaskClickListener, OnDeletingListener onDeletingListener) {
        this.tasks = tasks;
        this.onTaskClickListener = onTaskClickListener;
        tasksForDeleting = new ArrayList<>();
        this.onDeletingListener = onDeletingListener;
    }

    @Override
    public DayTaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DayTaskViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false));
    }

    public ArrayList<String> getTasksForDeleting() {
        return tasksForDeleting;
    }

    public boolean handleBackPress() {
        boolean handled = deleting;
        if (deleting) {
            setDeletingEnabled(false);
        }
        return handled;
    }

    @Override
    public void onBindViewHolder(final DayTaskViewHolder holder, int position) {

        final Task t = tasks.get(holder.getAdapterPosition());

        View.OnClickListener onSubtaskClick;
        View.OnLongClickListener onSubtaskLongClick;

        if (deleting) {
            holder.deletingCheckbox.setVisibility(View.VISIBLE);
            holder.deletingCheckbox.setChecked(longClicked == holder.getAdapterPosition());
            onSubtaskClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.deletingCheckbox.setChecked(!holder.deletingCheckbox.isChecked());
                }
            };
            onSubtaskLongClick = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClicked = holder.getAdapterPosition();
                    setDeletingEnabled(false);
                    return true;
                }
            };
        } else {
            holder.deletingCheckbox.setVisibility(View.GONE);
            holder.deletingCheckbox.setChecked(false);
            onSubtaskClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTaskClickListener.onClick(t);
                }
            };
            onSubtaskLongClick = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClicked = holder.getAdapterPosition();
                    setDeletingEnabled(true);
                    return true;
                }
            };
        }
        holder.nameTextView.setText(t.getName());
//TODO big bad crutch!
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

    private void setDeletingEnabled(boolean enabled) {
        deleting = enabled;
        onDeletingListener.onDeletingDisplay(enabled);
        notifyDataSetChanged();
    }

    public interface OnTaskClickListener {
        void onClick(Task task);
    }

    public interface OnDeletingListener {
        void onDeletingDisplay(boolean displaying);

        void onDeletingTasksNumberChanged(int number);
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
            deletingCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Task t = tasks.get(getAdapterPosition());
                    if (isChecked) {
                        tasksForDeleting.add(t.getId());
                    } else {
                        tasksForDeleting.remove(t.getId());
                    }
                    onDeletingListener.onDeletingTasksNumberChanged(tasksForDeleting.size());
                }
            });
        }
    }
}

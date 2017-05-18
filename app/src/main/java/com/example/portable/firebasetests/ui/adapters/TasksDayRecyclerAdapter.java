package com.example.portable.firebasetests.ui.adapters;

import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.core.FirebaseObserver;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;

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
        final Tag tag = FirebaseObserver.getInstance().getTags().getById(t.getTagId());
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
        if (tag != null) {
            holder.tagTextView.setText(tag.getName());
            holder.tagTextView.setTextColor((int) tag.getColor());
            Drawable drawable = holder.progressBar.getProgressDrawable();
            drawable.setColorFilter(new LightingColorFilter(0xFF000000, (int) tag.getColor()));
        }
        if (t.getSubTasks().size() != 0) {
            holder.taskDone.setVisibility(View.GONE);
            inflateSubtasks(holder);
        } else {
            holder.taskDone.setVisibility(View.VISIBLE);
            holder.taskDone.setImageDrawable(ContextCompat.getDrawable(FireTasksApp.getInstance(), t.isDone() ? R.drawable.taskdone : R.drawable.taskundone));
            holder.subtasksLayout.removeAllViews();
        }
        holder.nameTextView.setText(t.getName());
        holder.progressBar.setProgress(t.getProgress());
        holder.rootView.setOnClickListener(onSubtaskClick);
        holder.rootView.setOnLongClickListener(onSubtaskLongClick);
    }

    private void inflateSubtasks(DayTaskViewHolder holder) {
        Task t = tasks.get(holder.getAdapterPosition());
        int length = t.getSubTasks().size() < MAX_SUBTASKS_DISPLAYING ? t.getSubTasks().size() : MAX_SUBTASKS_DISPLAYING;
        holder.subtasksLayout.removeAllViews();
        for (int i = 0; i < length; i++) {
            View subtaskView = LayoutInflater.from(FireTasksApp.getInstance()).inflate(R.layout.item_subtasks_preview, null);
            ImageView check = (ImageView) subtaskView.findViewById(R.id.subtask_done_imv);
            TextView text = (TextView) subtaskView.findViewById(R.id.subtask_tv);
            text.setText(t.getSubTasks().get(i).getName());
            check.setImageDrawable(ContextCompat.getDrawable(FireTasksApp.getInstance(), t.getSubTasks().get(i).isDone() ? R.drawable.taskdone : R.drawable.taskundone));
            holder.subtasksLayout.addView(subtaskView);
        }
        holder.moreDots.setVisibility(t.getSubTasks().size() > MAX_SUBTASKS_DISPLAYING ? View.VISIBLE : View.GONE);
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
        ImageView moreDots;
        ProgressBar progressBar;
        CheckBox deletingCheckbox;
        ImageView taskDone;
        LinearLayout subtasksLayout;
        View rootView;

        DayTaskViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            tagTextView = (TextView) itemView.findViewById(R.id.tag_tv);
            deletingCheckbox = (CheckBox) itemView.findViewById(R.id.deleting_checkbox);
            nameTextView = (TextView) itemView.findViewById(R.id.task_name_tv);
            subtasksLayout = (LinearLayout) itemView.findViewById(R.id.subtasks_container);
            moreDots = (ImageView) itemView.findViewById(R.id.more_dots_tv);
            taskDone = (ImageView) itemView.findViewById(R.id.task_done_imv);
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

package com.example.portable.firebasetests.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.listeners.OnDateIdentifiedListener;
import com.example.portable.firebasetests.listeners.OnTaskClickListener;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.model.TasksDay;
import com.example.portable.firebasetests.view_holders.TaskChildViewHolder;
import com.example.portable.firebasetests.view_holders.TaskParentViewHolder;

import java.util.List;

import static com.example.portable.firebasetests.AppCompatColors.getColor;
import static com.example.portable.firebasetests.TimeUtils.isDayBefore;

/**
 * Created by Salenko Vsevolod on 07.02.2017.
 */

public class TasksExpandableAdapter extends ExpandableRecyclerAdapter<TaskParentViewHolder, TaskChildViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private OnDateIdentifiedListener onGroupClickListener;
    private OnTaskClickListener onTaskClickListener, onTaskLongClickListener;

    public TasksExpandableAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void setOnGroupClickListener(OnDateIdentifiedListener onGroupClickListener) {
        this.onGroupClickListener = onGroupClickListener;
    }

    @Override
    public TaskParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.group_tasks_list, viewGroup, false);
        return new TaskParentViewHolder(view);
    }

    @Override
    public TaskChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.item_tasks_list, viewGroup, false);
        return new TaskChildViewHolder(view);
    }


    @Override
    public void onBindParentViewHolder(TaskParentViewHolder parentViewHolder, final int i, Object o) {
        final TasksDay dayOfWeek = (TasksDay) o;
        parentViewHolder.dayNameTextView.setText(dayOfWeek.getName());
        parentViewHolder.cardView.setCardBackgroundColor(getGroupColor(dayOfWeek.getChildObjectList()));
        parentViewHolder.dateNameTextView.setText(dayOfWeek.getDateString());
        if (dayOfWeek.getChildObjectList().isEmpty()) {
            parentViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onGroupClickListener.onIdentified(dayOfWeek.getYear(), dayOfWeek.getWeek(), dayOfWeek.getDay());
                }
            });

        }
        parentViewHolder.plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGroupClickListener.onIdentified(dayOfWeek.getYear(), dayOfWeek.getWeek(), dayOfWeek.getDay());
            }
        });
    }

    @Override
    public void onBindChildViewHolder(TaskChildViewHolder childViewHolder, final int i, Object o) {
        final Task task = (Task) o;
        childViewHolder.taskCardView.setCardBackgroundColor(getTaskColor(task));
        childViewHolder.description.setText(task.getDescription());
        childViewHolder.time.setText(task.isCompleted() ? task.getTimeString() : "");
        childViewHolder.tag.setText(TagsColors.getTags().get((int) task.getTagIndex()).getName());
        childViewHolder.tagCardView.setCardBackgroundColor(TagsColors.getTags().get((int) task.getTagIndex()).getColor());
        childViewHolder.taskCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTaskClickListener.onClick(task);
            }
        });
        childViewHolder.taskCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onTaskLongClickListener.onClick(task);
                return false;
            }
        });
    }

    private int getTaskColor(Task task) {
        if (isDayBefore(task.getTimeStamp()) && !task.isCompleted()) {
            return getColor(R.color.failedTask, context);
        }
        if (!task.isCompleted()) {
            return getColor(R.color.undoneTask, context);
        }
        return getColor(R.color.doneTask, context);
    }

    private int getGroupColor(List<Object> tasks) {
        if (tasks.isEmpty()) {
            return getColor(R.color.emptyDay, context);
        }
        boolean isInPast = isDayBefore(((Task) tasks.get(0)).getTimeStamp());
        for (Object o : tasks) {
            Task t = (Task) o;
            if (t.isCompleted()) {
                continue;
            }
            return isInPast ? getColor(R.color.failedDay, context) : getColor(R.color.undoneDay, context);
        }
        return getColor(R.color.doneDay, context);

    }

    public void setOnTaskClickListener(OnTaskClickListener onTaskClickListener) {
        this.onTaskClickListener = onTaskClickListener;
    }

    public void setOnTaskLongClickListener(OnTaskClickListener onTaskLongClickListener) {
        this.onTaskLongClickListener = onTaskLongClickListener;
    }
}

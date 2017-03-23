package com.example.portable.firebasetests.ui.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.model.TasksDay;
import com.example.portable.firebasetests.ui.fragments.TasksWeekFragment;

import java.util.List;

import static com.example.portable.firebasetests.utils.TimeUtils.isInPast;

/**
 * Created by Salenko Vsevolod on 07.02.2017.
 */

public class TasksExpandableAdapter extends ExpandableRecyclerAdapter<TasksExpandableAdapter.TaskParentViewHolder, TasksExpandableAdapter.TaskChildViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private TasksWeekFragment.OnDateIdentifiedListener onGroupClickListener;
    private OnTaskClickListener onTaskClickListener, onTaskLongClickListener;

    public TasksExpandableAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void setOnGroupClickListener(TasksWeekFragment.OnDateIdentifiedListener onGroupClickListener) {
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
        childViewHolder.name.setText(task.getName());
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
        int res;
        if (isInPast(task.getTimeStamp()) && !task.isCompleted()) {
            res = ContextCompat.getColor(context, R.color.failedTask);
        } else if (!task.isCompleted()) {
            res = ContextCompat.getColor(context, R.color.undoneTask);
        } else {
            res = ContextCompat.getColor(context, R.color.doneTask);
        }
        return res;
    }

    //TODO refactor it, maybe add some method to model
    private int getGroupColor(List<Object> tasks) {
        if (tasks.isEmpty()) {
            return ContextCompat.getColor(context, R.color.emptyDay);
        }
        boolean isInPast = isInPast(((Task) tasks.get(0)).getTimeStamp());
        for (Object o : tasks) {
            Task t = (Task) o;
            if (t.isCompleted()) {
                continue;
            }
            return isInPast ? ContextCompat.getColor(context, R.color.failedDay) : ContextCompat.getColor(context, R.color.undoneDay);
        }
        return ContextCompat.getColor(context, R.color.doneDay);

    }

    public void setOnTaskClickListener(OnTaskClickListener onTaskClickListener) {
        this.onTaskClickListener = onTaskClickListener;
    }

    public void setOnTaskLongClickListener(OnTaskClickListener onTaskLongClickListener) {
        this.onTaskLongClickListener = onTaskLongClickListener;
    }

    public interface OnTaskClickListener {
        void onClick(Task task);
    }

    public class TaskChildViewHolder extends ChildViewHolder {
        public CardView taskCardView, tagCardView;
        public TextView name, tag;

        public TaskChildViewHolder(View itemView) {
            super(itemView);
            taskCardView = (CardView) itemView.findViewById(R.id.ItemTaskCardView);
            tagCardView = (CardView) itemView.findViewById(R.id.itemTaskTagCardView);
            name = (TextView) itemView.findViewById(R.id.itemTaskDescriptionView);
            tag = (TextView) itemView.findViewById(R.id.itemTaskTagTextView);
        }
    }

    public class TaskParentViewHolder extends ParentViewHolder {
        public CardView cardView;
        public TextView dayNameTextView;
        public TextView dateNameTextView;
        public ImageView plusImageView;

        public TaskParentViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.groupDayOfWeekCardView);
            dayNameTextView = (TextView) itemView.findViewById(R.id.groupTasksTextView);
            dateNameTextView = (TextView) itemView.findViewById(R.id.groupDateTextView);
            plusImageView = (ImageView) itemView.findViewById(R.id.groupTasksImageView);
        }
    }
}

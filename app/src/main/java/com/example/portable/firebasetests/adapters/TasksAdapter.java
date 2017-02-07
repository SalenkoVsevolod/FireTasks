package com.example.portable.firebasetests.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.listeners.OnDateIdentifiedListener;
import com.example.portable.firebasetests.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.portable.firebasetests.AppCompatColors.getColor;
import static com.example.portable.firebasetests.TimeUtils.isDayBefore;

/**
 * Created by Salenko Vsevolod on 01.02.2017.
 */

public class TasksAdapter extends BaseExpandableListAdapter {
    private static final String[] days = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private OnDateIdentifiedListener onImageClickListener;
    private HashMap<String, ArrayList<Task>> tasksMap;
    private LayoutInflater layoutInflater;
    private Context context;

    private TasksAdapter() {
        tasksMap = new HashMap<>();
        for (String day : days) {
            tasksMap.put(day, new ArrayList<Task>());
        }
    }

    public TasksAdapter(Context context, ArrayList<Task> tasksArrayList) {
        this();
        Calendar calendar = Calendar.getInstance();
        for (Task task : tasksArrayList) {
            calendar.setTimeInMillis(task.getTimeStamp());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == 7) {
                dayOfWeek = 0;
            }
            tasksMap.get(days[dayOfWeek]).add(task);
        }
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getGroupCount() {
        return days.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return tasksMap.get(days[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return tasksMap.get(days[groupPosition]);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return tasksMap.get(days[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View
            convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.group_tasks_list, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.groupTasksTextView);
        textView.setText(days[groupPosition]);
        convertView.setBackgroundColor(getGroupColor(groupPosition));
        ImageView addView = (ImageView) convertView.findViewById(R.id.groupTasksImageView);
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClickListener.onIdentified(getGroupMillis(groupPosition));
            }
        });

        return convertView;
    }
/*
    //TODO crutch
    private int getDayOfWeek(int group) {

        if (group == Calendar.SUNDAY) {
            return 1;
        } else {
            return group + 2;
        }
    }*/

    @SuppressWarnings("unchecked")
    private int getGroupColor(int groupId) {
        ArrayList<Task> tasks = (ArrayList<Task>) getGroup(groupId);
        if (tasks.isEmpty()) {
            return getColor(R.color.emptyDay, context);
        }
        boolean isInPast = isDayBefore(tasks.get(0).getTimeStamp());
        for (Task t : tasks) {
            if (t.isCompleted()) {
                continue;
            }
            return isInPast ? getColor(R.color.failedDay, context) : getColor(R.color.undoneDay, context);
        }
        return getColor(R.color.doneDay, context);

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_tasks_list, parent, false);
        }
        Task task = (Task) getChild(groupPosition, childPosition);
        CardView item = (CardView) convertView.findViewById(R.id.ItemTaskCardView);
        item.setCardBackgroundColor(getTaskColor(task));
        TextView description = (TextView) convertView.findViewById(R.id.itemTaskDescriptionView);
        description.setText(task.getDescription());
        TextView time = (TextView) convertView.findViewById(R.id.itemTaskTimeView);

        time.setText(task.isTimeSpecified() ? task.getTimeString() : "");
        CardView tag = (CardView) convertView.findViewById(R.id.itemTaskTagCardView);
        tag.setCardBackgroundColor((int) task.getTag().getColor());
        TextView tagText = (TextView) convertView.findViewById(R.id.itemTaskTagTextView);
        tagText.setText(task.getTag().getName());
        return convertView;
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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public long getGroupMillis(int groupIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK, groupIndex);
        return calendar.getTimeInMillis();
    }

    @SuppressWarnings("unchecked")
    public boolean isChildEmpty(int group) {
        ArrayList<Task> tasks = (ArrayList<Task>) getGroup(group);
        return tasks.isEmpty();
    }

    public void setOnImageClickListener(OnDateIdentifiedListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }
}

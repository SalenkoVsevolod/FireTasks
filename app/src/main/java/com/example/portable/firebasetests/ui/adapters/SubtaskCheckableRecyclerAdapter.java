package com.example.portable.firebasetests.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.model.SubTask;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class SubtaskCheckableRecyclerAdapter extends RecyclerView.Adapter<SubtaskCheckableRecyclerAdapter.CheckableVH> {
    private ArrayList<SubTask> subTasks;
    private OnSubtaskCheckListener listener;

    public SubtaskCheckableRecyclerAdapter(ArrayList<SubTask> subTasks, OnSubtaskCheckListener listener) {
        this.subTasks = subTasks;
        this.listener = listener;
    }

    @Override
    public CheckableVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CheckableVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtask_checkable, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckableVH holder, int position) {
        holder.checkBox.setText(subTasks.get(position).getName());
        holder.checkBox.setChecked(subTasks.get(position).isDone());
        int priorityId = (int) subTasks.get(position).getPriority();
        holder.priority.setText(FireTasksApp.getInstance().getResources().getStringArray(R.array.priorities)[priorityId]);
        holder.priority.setBackgroundColor(ContextCompat.getColor(FireTasksApp.getInstance(), SubTask.PRIORITY_COLORS_IDS.get(priorityId)));
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    public interface OnSubtaskCheckListener {
        void onCheck(SubTask subTask, boolean checked);
    }

    class CheckableVH extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView priority;

        public CheckableVH(View itemView) {
            super(itemView);
            priority = (TextView) itemView.findViewById(R.id.priority_tv);
            checkBox = (CheckBox) itemView.findViewById(R.id.subtask_checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onCheck(subTasks.get(getAdapterPosition()), isChecked);
                }
            });
        }
    }
}

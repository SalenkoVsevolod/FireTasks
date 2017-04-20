package com.example.portable.firebasetests.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.model.SubTask;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class SubtaskClickableRecyclerAdapter extends RecyclerView.Adapter<SubtaskClickableRecyclerAdapter.SubtaskClickableVH> {
    private ArrayList<SubTask> subTasks;
    private OnSubtaskInteractionListener listener;

    public SubtaskClickableRecyclerAdapter(ArrayList<SubTask> subTasks, OnSubtaskInteractionListener listener) {
        this.subTasks = subTasks;
        this.listener = listener;
    }

    @Override
    public SubtaskClickableVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubtaskClickableVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtask_clickable, parent, false));
    }

    @Override
    public void onBindViewHolder(SubtaskClickableVH holder, int position) {
        holder.subtask.setText(subTasks.get(position).getName());
        int priority = (int) subTasks.get(position).getPriority();
        holder.priority.setText(SubTask.PRIORITIES.get(priority));
        holder.priority.setBackgroundColor(ContextCompat.getColor(FireTasksApp.getInstance(), SubTask.PRIORITY_COLORS_IDS.get(priority)));
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    public interface OnSubtaskInteractionListener {
        void onSubtaskClick(SubTask subTask);

        void onDeleteClick(SubTask subTask);
    }

    class SubtaskClickableVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subtask, priority;

        SubtaskClickableVH(View itemView) {
            super(itemView);
            subtask = (TextView) itemView.findViewById(R.id.subtask_name);
            priority = (TextView) itemView.findViewById(R.id.priority_tv);
            itemView.findViewById(R.id.edit_item).setOnClickListener(this);
            itemView.findViewById(R.id.delete_subtask).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edit_item:
                    listener.onSubtaskClick(subTasks.get(getAdapterPosition()));
                    break;
                case R.id.delete_subtask:
                    listener.onDeleteClick(subTasks.get(getAdapterPosition()));
                    break;
            }
        }
    }
}

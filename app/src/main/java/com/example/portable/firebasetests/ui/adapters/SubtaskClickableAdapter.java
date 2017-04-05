package com.example.portable.firebasetests.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.SubTask;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class SubtaskClickableAdapter extends RecyclerView.Adapter<SubtaskClickableAdapter.SubtaskClickableVH> {
    private ArrayList<SubTask> subTasks;
    private OnSubtaskClickListener listener;

    public SubtaskClickableAdapter(ArrayList<SubTask> subTasks, OnSubtaskClickListener listener) {
        this.subTasks = subTasks;
        this.listener = listener;
    }

    @Override
    public SubtaskClickableVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubtaskClickableVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtask_clickable, parent, false));
    }

    @Override
    public void onBindViewHolder(SubtaskClickableVH holder, int position) {
        holder.subtask.setText(subTasks.get(position).getDescription());
        holder.priority.setText(SubTask.PRIORITIES.get((int) subTasks.get(position).getPriority()));
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    public interface OnSubtaskClickListener {
        void onClick(SubTask subTask);
    }

    class SubtaskClickableVH extends RecyclerView.ViewHolder {
        TextView subtask, priority;

        public SubtaskClickableVH(View itemView) {
            super(itemView);
            subtask = (TextView) itemView.findViewById(R.id.subtask_name);
            priority = (TextView) itemView.findViewById(R.id.priority_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(subTasks.get(getAdapterPosition()));
                }
            });
        }
    }
}

package com.example.portable.firebasetests.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.SubTask;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 05.04.2017.
 */

public class SubtaskCheckableAdapter extends RecyclerView.Adapter<SubtaskCheckableAdapter.CheckableVH> {
    private ArrayList<SubTask> subTasks;
    private OnSubtaskCheckListener listener;

    public SubtaskCheckableAdapter(ArrayList<SubTask> subTasks, OnSubtaskCheckListener listener) {
        this.subTasks = subTasks;
        this.listener = listener;
    }

    @Override
    public CheckableVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CheckableVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.subtask_checkout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckableVH holder, int position) {
        holder.checkBox.setText(subTasks.get(position).getDescription());
        holder.checkBox.setChecked(subTasks.get(position).isDone());
        holder.priority.setText(SubTask.PRIORITIES.get((int) subTasks.get(position).getPriority()));
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

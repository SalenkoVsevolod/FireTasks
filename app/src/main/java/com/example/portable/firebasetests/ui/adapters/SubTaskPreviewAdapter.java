package com.example.portable.firebasetests.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.SubTask;

import java.util.List;

/**
 * Created by Portable on 27.01.2017.
 */

public class SubTaskPreviewAdapter extends RecyclerView.Adapter<SubTaskPreviewAdapter.ViewHolder> {
    private List<SubTask> subTasks;

    public SubTaskPreviewAdapter(List<SubTask> subTasks) {
        this.subTasks = subTasks;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtasks_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.checkBox.setText(subTasks.get(position).getDescription());
        holder.checkBox.setChecked(subTasks.get(position).isDone());
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;

        private ViewHolder(View rootView) {
            super(rootView);
            checkBox = (CheckBox) rootView.findViewById(R.id.subtask_checkbox);
        }

    }
}

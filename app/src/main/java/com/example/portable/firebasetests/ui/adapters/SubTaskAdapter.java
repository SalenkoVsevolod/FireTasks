package com.example.portable.firebasetests.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.ui.OnListItemClickListener;
import com.example.portable.firebasetests.model.SubTask;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Portable on 27.01.2017.
 */

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {
    private ArrayList<SubTask> subTasks;
    private OnSubTaskClickListener subTaskClickListener;
    private OnSubTaskCheckBoxCliCkListener subTaskCheckBoxCliCkListener;

    public SubTaskAdapter(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;

    }

    public SubTaskAdapter(ArrayList<SubTask> subTasks, OnSubTaskClickListener subTaskClickListener) {
        this(subTasks);
        this.subTaskClickListener = subTaskClickListener;
    }

    public SubTaskAdapter(ArrayList<SubTask> subTasks, OnSubTaskCheckBoxCliCkListener onSubTaskCheckBoxCliCkListener) {
        this(subTasks);
        this.subTaskCheckBoxCliCkListener = onSubTaskCheckBoxCliCkListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtasks_list, parent, false);
        return subTaskClickListener == null ? new ViewHolder(view, subTaskCheckBoxCliCkListener) : new ViewHolder(view, subTaskClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.index = holder.getAdapterPosition();
        holder.checkBox.setText(subTasks.get(position).getDescription());
        holder.checkBox.setChecked(subTasks.get(position).isDone());
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }


    public SubTask getItem(int index) {
        return subTasks.get(index);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public int index;
        public CheckBox checkBox;
        public TextView textView;

        private ViewHolder(View rootView) {
            super(rootView);
            checkBox = (CheckBox) rootView.findViewById(R.id.subtask_checkbox);
            textView = (TextView) rootView.findViewById(R.id.subtask_tv);
        }

        public ViewHolder(View rootView, final OnSubTaskClickListener subTaskClickListener) {
            this(rootView);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subTaskClickListener.onClick(subTasks.get(getAdapterPosition()));
                }
            });
            checkBox.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }

        public ViewHolder(View rootView, final OnSubTaskCheckBoxCliCkListener listener) {
            this(rootView);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onClick(subTasks.get(getAdapterPosition()), isChecked);
                }
            });
            checkBox.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
    }

    public interface OnSubTaskClickListener {
        void onClick(SubTask subTask);
    }

    public interface OnSubTaskCheckBoxCliCkListener {
        void onClick(SubTask subTask, boolean checked);
    }
}

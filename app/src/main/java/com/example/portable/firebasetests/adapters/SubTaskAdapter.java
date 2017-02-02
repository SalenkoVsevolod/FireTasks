package com.example.portable.firebasetests.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.listeners.OnMyItemLongClickListener;
import com.example.portable.firebasetests.listeners.OnSubTaskClickListener;
import com.example.portable.firebasetests.model.SubTask;

import java.util.ArrayList;

/**
 * Created by Portable on 27.01.2017.
 */

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {
    private ArrayList<SubTask> subTasks;
    private OnMyItemLongClickListener longClickListener;

    public SubTaskAdapter(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtasks_list, parent, false);
        OnSubTaskClickListener listener = new OnSubTaskClickListener() {
            @Override
            public void onClick(int index, boolean checked) {
                subTasks.get(index).setDone(checked);
            }
        };
        return new ViewHolder(view, listener, longClickListener);
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

    public void setLongClickListener(OnMyItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public SubTask getItem(int index) {
        return subTasks.get(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public int index;
        public CheckBox checkBox;

        public ViewHolder(View rootView, final OnSubTaskClickListener onSubTaskClickListener, final OnMyItemLongClickListener onMyItemLongClickListener) {
            super(rootView);
            checkBox = (CheckBox) rootView.findViewById(R.id.itemSubTaskCheckbox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSubTaskClickListener.onClick(index, checkBox.isChecked());
                }
            });
            checkBox.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onMyItemLongClickListener.onLongClick(index);
                    return false;
                }
            });
        }
    }
}

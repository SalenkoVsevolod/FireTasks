package com.example.portable.firebasetests.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.model.SubTask;

import java.util.List;

/**
 * Created by Portable on 27.01.2017.
 */

public class SubTaskPreviewAdapter extends RecyclerView.Adapter<SubTaskPreviewAdapter.ViewHolder> {
    private List<SubTask> subTasks;
    private View.OnClickListener recyclerClickListener;
    private View.OnLongClickListener recyclerLongClickListener;

    public SubTaskPreviewAdapter(List<SubTask> subTasks, View.OnClickListener recyclerClickListener, View.OnLongClickListener recyclerLongClickListener) {
        this.subTasks = subTasks;
        this.recyclerClickListener = recyclerClickListener;
        this.recyclerLongClickListener = recyclerLongClickListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtasks_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.subtaskText.setText(subTasks.get(position).getDescription());
        holder.subtaskDone.setImageDrawable(ContextCompat.getDrawable(FireTasksApp.getInstance(), subTasks.get(position).isDone() ? R.drawable.taskdone : R.drawable.taskundone));
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView subtaskText;
        public ImageView subtaskDone;

        private ViewHolder(View rootView) {
            super(rootView);
            subtaskText = (TextView) rootView.findViewById(R.id.subtask_tv);
            subtaskDone = (ImageView) rootView.findViewById(R.id.subtask_done_imv);
            rootView.setOnClickListener(recyclerClickListener);
            rootView.setOnLongClickListener(recyclerLongClickListener);
        }
    }
}

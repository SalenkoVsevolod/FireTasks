package com.example.portable.firebasetests.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 22.03.2017.
 */

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private ArrayList<Remind> reminds;
    private OnRemindClickListener listener;

    public ReminderAdapter(ArrayList<Remind> reminds, OnRemindClickListener listener) {
        this.reminds = reminds;
        this.listener = listener;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReminderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        holder.time.setText(reminds.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return reminds.size();
    }

    public interface OnRemindClickListener {
        void onClick(Remind remind);
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        private TextView time;

        public ReminderViewHolder(View item) {
            super(item);
            time = (TextView) item.findViewById(R.id.tagTextView);
            if (listener != null) {
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(reminds.get(getAdapterPosition()));
                    }
                });
            }
        }
    }
}


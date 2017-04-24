package com.example.portable.firebasetests.ui.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 22.03.2017.
 */

public class ReminderDisplayRecyclerAdapter extends RecyclerView.Adapter<ReminderDisplayRecyclerAdapter.ReminderViewHolder> {
    private ArrayList<Remind> reminds;
    private OnRemindClickListener listener;

    public ReminderDisplayRecyclerAdapter(ArrayList<Remind> reminds, OnRemindClickListener listener) {
        this.reminds = reminds;
        this.listener = listener;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout res = new FrameLayout(parent.getContext());
        TextView textView = new TextView(parent.getContext());
        textView.setId(R.id.reminder_tv);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextColor(Color.BLACK);
        params.setMargins(0, 0, 0, 32);
        textView.setLayoutParams(params);
        res.addView(textView);
        return new ReminderViewHolder(res);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        holder.time.setText(reminds.get(holder.getAdapterPosition()).toString());
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

        ReminderViewHolder(View item) {
            super(item);
            time = (TextView) item.findViewById(R.id.reminder_tv);
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


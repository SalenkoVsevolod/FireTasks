package com.example.portable.firebasetests.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;

import java.util.ArrayList;

/**
 * Created by Salenko Vsevolod on 11.04.2017.
 */

public class ReminderDeletableAdapter extends RecyclerView.Adapter<ReminderDeletableAdapter.ReminderVH> {
    private ArrayList<Remind> reminds;
    private OnReminderInteractionListener listener;

    public ReminderDeletableAdapter(ArrayList<Remind> reminds, OnReminderInteractionListener listener) {
        this.reminds = reminds;
        this.listener = listener;
    }

    @Override
    public ReminderVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReminderVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder_deleteable, parent, false));
    }

    @Override
    public void onBindViewHolder(ReminderVH holder, int position) {
        SpannableString content = new SpannableString(reminds.get(holder.getAdapterPosition()).toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.text.setText(content);
    }

    @Override
    public int getItemCount() {
        return reminds.size();
    }

    public interface OnReminderInteractionListener {
        void deleteClick(Remind remind);

        void reminderClick(Remind remind);
    }

    public class ReminderVH extends RecyclerView.ViewHolder {
        TextView text;

        public ReminderVH(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.reminder_tv);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.reminderClick(reminds.get(getAdapterPosition()));
                }
            });
            itemView.findViewById(R.id.reminder_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.deleteClick(reminds.get(getAdapterPosition()));
                }
            });
        }
    }
}

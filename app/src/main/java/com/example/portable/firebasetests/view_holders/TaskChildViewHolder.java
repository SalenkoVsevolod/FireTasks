package com.example.portable.firebasetests.view_holders;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.example.portable.firebasetests.R;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class TaskChildViewHolder extends ChildViewHolder {
    public CardView taskCardView, tagCardView;
    public TextView description, time, tag;

    public TaskChildViewHolder(View itemView) {
        super(itemView);
        taskCardView = (CardView) itemView.findViewById(R.id.ItemTaskCardView);
        tagCardView = (CardView) itemView.findViewById(R.id.itemTaskTagCardView);
        description = (TextView) itemView.findViewById(R.id.itemTaskDescriptionView);
        time = (TextView) itemView.findViewById(R.id.itemTaskTimeView);
        tag = (TextView) itemView.findViewById(R.id.itemTaskTagTextView);
    }
}

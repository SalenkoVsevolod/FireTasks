package com.example.portable.firebasetests.view_holders;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.example.portable.firebasetests.R;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class TaskParentViewHolder extends ParentViewHolder {
    public CardView cardView;
    public TextView dayNameTextView;
    public TextView dateNameTextView;
    public ImageView plusImageView;

    public TaskParentViewHolder(View itemView) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.groupDayOfWeekCardView);
        dayNameTextView = (TextView) itemView.findViewById(R.id.groupTasksTextView);
        dateNameTextView = (TextView) itemView.findViewById(R.id.groupDateTextView);
        plusImageView = (ImageView) itemView.findViewById(R.id.groupTasksImageView);
    }

}

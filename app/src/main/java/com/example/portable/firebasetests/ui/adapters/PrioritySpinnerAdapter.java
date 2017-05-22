package com.example.portable.firebasetests.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.model.SubTask;

/**
 * Created by Salenko Vsevolod on 12.04.2017.
 */

public class PrioritySpinnerAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return FireTasksApp.getInstance().getResources().getStringArray(R.array.priorities).length;
    }

    @Override
    public Object getItem(int position) {
        return FireTasksApp.getInstance().getResources().getStringArray(R.array.priorities)[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_priority, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.priority_tv);
        tv.setText(FireTasksApp.getInstance().getResources().getStringArray(R.array.priorities)[position]);
        tv.setBackgroundColor(ContextCompat.getColor(parent.getContext(), SubTask.PRIORITY_COLORS_IDS.get(position)));
        return convertView;
    }
}

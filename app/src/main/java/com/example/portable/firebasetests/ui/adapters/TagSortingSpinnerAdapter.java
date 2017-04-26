package com.example.portable.firebasetests.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Tag;

import java.util.ArrayList;

/**
 * Created by Portable on 26.01.2017.
 */

public class TagSortingSpinnerAdapter extends BaseAdapter {
    private ArrayList<Tag> tags;
    private Context context;

    public TagSortingSpinnerAdapter(Context context, ArrayList<Tag> tags) {
        this.tags = tags;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            FrameLayout frameLayout = new FrameLayout(context);
            TextView tag = new TextView(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 8);
            tag.setTextSize(18);
            tag.setId(R.id.tagTextView);
            tag.setLayoutParams(params);
            frameLayout.addView(tag);
            view = frameLayout;
        }

        final Tag tag = (Tag) getItem(position);
        TextView item = (TextView) view.findViewById(R.id.tagTextView);
        item.setTextColor((int) tag.getColor());
        item.setText(tag.getName());
        return view;
    }

}

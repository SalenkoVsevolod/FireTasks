package com.example.portable.firebasetests.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Tag;

import java.util.ArrayList;

/**
 * Created by Portable on 26.01.2017.
 */

public class TagAdapter extends BaseAdapter {
    private ArrayList<Tag> tags;
    private LayoutInflater inflater;


    public TagAdapter(Context context, ArrayList<Tag> tags) {
        this.tags = tags;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = inflater.inflate(R.layout.item_tag, parent, false);
        }
        final Tag tag = (Tag) getItem(position);
        TextView item = (TextView) view.findViewById(R.id.tagTextView);
        CardView background = (CardView) view.findViewById(R.id.tag_cardview);
        background.setCardBackgroundColor((int) tag.getColor());
        item.setText(tag.getName());
        return view;
    }

}

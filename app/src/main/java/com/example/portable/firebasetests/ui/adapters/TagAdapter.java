package com.example.portable.firebasetests.ui.adapters;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Tag;

import java.util.ArrayList;

/**
 * Created by Black on 10.04.2017.
 */

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagVH> {
    private ArrayList<Tag> tags;
    private OnTagInteractionListener listener;

    public TagAdapter(ArrayList<Tag> tags, OnTagInteractionListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    @Override
    public TagVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TagVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false));
    }

    @Override
    public void onBindViewHolder(TagVH holder, int position) {
        Tag tag = tags.get(holder.getAdapterPosition());
        holder.text.setText(tag.getName());
        holder.text.setTextColor((int) tag.getColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.editIcon.setImageTintList(ColorStateList.valueOf((int) tag.getColor()));
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(5, (int) tag.getColor());
        drawable.setCornerRadius(270f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.root.setBackground(drawable);
        } else {
            holder.root.setBackgroundDrawable(drawable);
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public interface OnTagInteractionListener {
        void clickOnTag(Tag tag);

        void clickOnEdit(Tag tag);
    }

    class TagVH extends RecyclerView.ViewHolder {
        View root;
        TextView text;
        ImageView editIcon;

        private TagVH(View rootView) {
            super(rootView);
            root = rootView;
            text = (TextView) rootView.findViewById(R.id.tag_text);
            editIcon = (ImageView) rootView.findViewById(R.id.tag_edit_iv);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.clickOnTag(tags.get(getAdapterPosition()));
                }
            });
            editIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.clickOnEdit(tags.get(getAdapterPosition()));
                }
            });
        }
    }
}

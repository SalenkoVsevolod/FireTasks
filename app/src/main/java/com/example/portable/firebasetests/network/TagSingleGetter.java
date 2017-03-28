package com.example.portable.firebasetests.network;

import android.os.AsyncTask;

import com.example.portable.firebasetests.model.Tag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 28.03.2017.
 */

public class TagSingleGetter extends AsyncTask<Void, Void, Void> {
    private String tagId;
    private OnTagGetListener listener;

    public TagSingleGetter(String tagId, OnTagGetListener listener) {
        this.tagId = tagId;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        FirebaseManager.getInstance().getTagsReference().child(tagId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                if (map == null) {
                    map = new HashMap<>();
                }
                Tag tag = new Tag(map);
                tag.setId(tagId);
                listener.onGet(tag);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return null;
    }


    public interface OnTagGetListener {
        void onGet(Tag tag);
    }
}

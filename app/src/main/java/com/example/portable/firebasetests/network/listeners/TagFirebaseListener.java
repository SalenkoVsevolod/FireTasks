package com.example.portable.firebasetests.network.listeners;

import android.os.AsyncTask;

import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 28.03.2017.
 */

public class TagFirebaseListener extends AsyncTask<Void, Void, Void> {
    private String tagId;
    private OnTagGetListener listener;
    private ValueEventListener valueEventListener;

    public TagFirebaseListener(String tagId, OnTagGetListener listener) {
        this.tagId = tagId;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                if (map == null) {
                    listener.onGet(null);
                } else {
                    Tag tag = new Tag(map);
                    tag.setId(tagId);
                    listener.onGet(tag);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseUtils.getInstance().getTagsReference().child(tagId).addValueEventListener(valueEventListener);
        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        FirebaseListenersManager.getInstance().getTagsReference().child(tagId).removeEventListener(valueEventListener);
    }

    public interface OnTagGetListener {
        void onGet(Tag tag);
    }
}

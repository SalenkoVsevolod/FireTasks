package com.example.portable.firebasetests.network.listeners;

import android.util.Log;

import com.example.portable.firebasetests.core.EntityList;
import com.example.portable.firebasetests.core.FirebaseObserver;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Salenko Vsevolod on 20.04.2017.
 */

public class TagsSyncTask extends FirebaseEntitySyncTask<Tag> {

    public TagsSyncTask() {
        super(FirebaseUtils.getInstance().getTagsReference());
    }

    @Override
    protected void onDataChanged(EntityList<Tag> synced) {
        FirebaseObserver.getInstance().getTags().sync(synced);
    }

    @Override
    protected EntityList<Tag> parseEntities(DataSnapshot dataSnapshot) {
        HashMap<String, Object> rawMap = (HashMap<String, Object>) dataSnapshot.getValue();
        if (rawMap == null) {
            rawMap = new HashMap<>();
        }
        EntityList<Tag> res = new EntityList<>();
        Set<String> ids = rawMap.keySet();
        for (String id : ids) {
            Tag tag = new Tag((HashMap<String, Object>) rawMap.get(id));
            tag.setId(id);
            res.add(tag);
            Log.i("tags", tag.getName());
        }
        return res;
    }
}
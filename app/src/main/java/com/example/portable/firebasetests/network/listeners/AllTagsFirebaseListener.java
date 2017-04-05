package com.example.portable.firebasetests.network.listeners;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Salenko Vsevolod on 28.03.2017.
 */

public class AllTagsFirebaseListener extends AsyncTask<Void, ArrayList<Tag>, Void> {
    private OnTagsSyncListener listener;
    private ValueEventListener valueEventListener;

    public AllTagsFirebaseListener(OnTagsSyncListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                if (map == null) {
                    map = new HashMap<>();
                }
                publishProgress(getTags(map));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FireTasksApp.getInstance(), "Connection cancelled", Toast.LENGTH_LONG).show();
            }
        };
        FirebaseUtils.getInstance().getTagsReference().addValueEventListener(valueEventListener);
        return null;
    }

    private ArrayList<Tag> getTags(HashMap<String, Object> map) {
        ArrayList<Tag> res = new ArrayList<>();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Tag tag = new Tag((HashMap<String, Object>) map.get(key));
            tag.setId(key);
            res.add(tag);
        }
        return res;
    }

    @Override
    protected void onProgressUpdate(ArrayList<Tag>... values) {
        super.onProgressUpdate(values);
        listener.onSync(values[0]);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        FirebaseUtils.getInstance().getTagsReference().removeEventListener(valueEventListener);
    }

    public interface OnTagsSyncListener {
        void onSync(ArrayList<Tag> tags);
    }
}

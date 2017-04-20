package com.example.portable.firebasetests.network.listeners;

import com.example.portable.firebasetests.core.EntityList;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

/**
 * Created by Salenko Vsevolod on 20.04.2017.
 */

public class RemindersSyncTask extends FirebaseEntitySyncTask<Remind> {

    public RemindersSyncTask() {
        super(FirebaseUtils.getInstance().getRemindersReference());
    }

    @Override
    protected void onDataChanged(EntityList<Remind> entities) {
        //TODO FirebaseObserver.getInstance().syncReminders(entities);
    }

    @Override
    protected EntityList<Remind> parseEntities(DataSnapshot dataSnapshot) {
        HashMap<String, Object> rawData = (HashMap<String, Object>) dataSnapshot.getValue();
        if (rawData == null) {
            rawData = new HashMap<>();
        }
        EntityList<Remind> res = new EntityList<>();
        for (String id : rawData.keySet()) {
            Remind remind = new Remind((HashMap<String, Object>) rawData.get(id));
            remind.setId(id);
            res.add(remind);
        }
        return res;
    }
}

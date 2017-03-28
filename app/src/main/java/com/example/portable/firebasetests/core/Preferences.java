package com.example.portable.firebasetests.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class Preferences {
    private static final String PREFERENCES = "prefs", USER_ID = "userId", LAST_RINGTONE = "ringtone";
    private static Preferences instance;
    private SharedPreferences preferences;

    private Preferences() {
        Log.i("creation", "app retrieved");
        preferences = FireTasksApp.getInstance().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    public void writeUserId(String id) {
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString(USER_ID, id);
        ed.apply();
    }

    public String readUserId() {
        return preferences.getString(USER_ID, null);
    }

    public void logout() {
        SharedPreferences.Editor ed = preferences.edit();
        ed.remove(USER_ID);
        ed.apply();
    }

    @Nullable
    public String readLastRingtone() {
        return preferences.getString(LAST_RINGTONE, null);
    }

    public void writeLastRingtone(String ringtone) {
        SharedPreferences.Editor ed = preferences.edit();
        ed.putString(LAST_RINGTONE, ringtone);
        ed.apply();
    }
}

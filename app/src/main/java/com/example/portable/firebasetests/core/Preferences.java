package com.example.portable.firebasetests.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class Preferences {
    private static final String PREFERENCES = "prefs", USER_ID = "userId", LAST_RINGTONE = "ringtone",
            LAST_OPENED = "lastOpened", DAY = "day";
    private static Preferences instance;
    private SharedPreferences preferences;

    private Preferences() {
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

    public void writeLastOpenedDay(int dayOfYear) {
        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt(DAY, dayOfYear);
        ed.apply();
    }

    public void writeWhenLastOpened(int timestamp) {
        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt(LAST_OPENED, timestamp);
        ed.apply();
    }

    public int readLastOpenedDay() {
        return preferences.getInt(DAY, -1);
    }

    public int readWhenLastOpened() {
        return preferences.getInt(LAST_OPENED, -1);
    }

    public void writeRemindCode(String remindId, int code) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(remindId, code);
        editor.apply();
    }

    public int readRemindCode(String remindId) {
        return preferences.getInt(remindId, -1);
    }

    public void removeRemindCode(String remindId) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(remindId);
        editor.apply();
    }
}

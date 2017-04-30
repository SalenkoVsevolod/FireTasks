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
    private SharedPreferences mPreferences;

    private Preferences() {
        mPreferences = FireTasksApp.getInstance().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
        }
        return instance;
    }

    public void writeUserId(String id) {
        SharedPreferences.Editor ed = mPreferences.edit();
        ed.putString(USER_ID, id);
        ed.apply();
    }

    public String readUserId() {
        return mPreferences.getString(USER_ID, null);
    }

    public void logout() {
        SharedPreferences.Editor ed = mPreferences.edit();
        ed.remove(USER_ID);
        ed.apply();
    }

    @Nullable
    public String readLastRingtone() {
        return mPreferences.getString(LAST_RINGTONE, null);
    }

    public void writeLastRingtone(String ringtone) {
        SharedPreferences.Editor ed = mPreferences.edit();
        ed.putString(LAST_RINGTONE, ringtone);
        ed.apply();
    }

    public void writeLastOpenedDay(int dayOfYear) {
        SharedPreferences.Editor ed = mPreferences.edit();
        ed.putInt(DAY, dayOfYear);
        ed.apply();
    }

    public void writeWhenLastOpened(int timestamp) {
        SharedPreferences.Editor ed = mPreferences.edit();
        ed.putInt(LAST_OPENED, timestamp);
        ed.apply();
    }

    public int readLastOpenedDay() {
        return mPreferences.getInt(DAY, -1);
    }

    public int readWhenLastOpened() {
        return mPreferences.getInt(LAST_OPENED, -1);
    }

    public void writeRemindCode(String remindId, int code) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(remindId, code);
        editor.apply();
    }

    public int readRemindCode(String remindId) {
        return mPreferences.getInt(remindId, -1);
    }

    public void removeRemindCode(String remindId) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(remindId);
        editor.apply();
    }
}

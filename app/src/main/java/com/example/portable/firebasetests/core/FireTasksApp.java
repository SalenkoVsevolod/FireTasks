package com.example.portable.firebasetests.core;

import android.app.Application;
import android.support.v4.content.ContextCompat;

/**
 * Created by Black on 18.03.2017.
 */

public class FireTasksApp extends Application {
    private static FireTasksApp instance;

    public static FireTasksApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public int getCompatColor(int id) {
        return ContextCompat.getColor(this, id);
    }
}

package com.example.portable.firebasetests.core;

import android.app.Application;

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
}

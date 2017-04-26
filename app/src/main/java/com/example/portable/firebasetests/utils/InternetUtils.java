package com.example.portable.firebasetests.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.portable.firebasetests.core.FireTasksApp;

/**
 * Created by Black on 19.04.2017.
 */

public class InternetUtils {
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) FireTasksApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }
}

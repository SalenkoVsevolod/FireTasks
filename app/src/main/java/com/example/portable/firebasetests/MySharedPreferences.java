package com.example.portable.firebasetests;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Salenko Vsevolod on 15.02.2017.
 */

public class MySharedPreferences {
    public static void writeUserId(Context context, String id) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.sharedPreferencesFileName), Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(context.getString(R.string.ID_SHARED_KEY), id);
        ed.apply();
    }

    public static String readUserId(Context context) {
        SharedPreferences sPref = context.getSharedPreferences(context.getString(R.string.sharedPreferencesFileName), Context.MODE_PRIVATE);
        return sPref.getString(context.getString(R.string.ID_SHARED_KEY), null);
    }
}

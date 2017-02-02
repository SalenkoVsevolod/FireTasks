package com.example.portable.firebasetests;

import android.content.Context;
import android.os.Build;

/**
 * Created by Salenko Vsevolod on 01.02.2017.
 */
public class AppCompatColors {
    //TODO crutch

    @SuppressWarnings("deprecation")
    public static int getColor(int id, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(id, null);
        } else {
            return context.getResources().getColor(id);
        }
    }
}

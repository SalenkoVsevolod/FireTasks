package com.example.portable.firebasetests;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.portable.firebasetests.ui.activities.LoginActivity;

public class InternetStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}

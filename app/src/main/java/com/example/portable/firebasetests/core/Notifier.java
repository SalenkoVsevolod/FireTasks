package com.example.portable.firebasetests.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.portable.firebasetests.broadcast_receivers.NotificationsBroadcastReceiver;
import com.example.portable.firebasetests.model.Remind;

/**
 * Created by Salenko Vsevolod on 27.01.2017.
 */

public class Notifier {

    public static void setAlarm(Remind remind) {
        Intent notificationIntent = new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class);
        if (remind.getTimeStamp() > System.currentTimeMillis()) {
            notificationIntent.putExtra(NotificationsBroadcastReceiver.REMIND, remind);
            int code = (int) remind.getTimeStamp();
            Preferences.getInstance().writeRemindCode(remind.getId(), code);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(FireTasksApp.getInstance(), code, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, remind.round(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, remind.round(), pendingIntent);
            }
        }
    }

    public static void removeAlarm(String remindId) {
        int code = Preferences.getInstance().readRemindCode(remindId);
        if (code != -1) {
            AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(PendingIntent.getBroadcast(FireTasksApp.getInstance(), code, new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));
        }
    }
}


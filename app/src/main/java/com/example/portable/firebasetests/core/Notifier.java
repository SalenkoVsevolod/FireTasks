package com.example.portable.firebasetests.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.portable.firebasetests.NotificationsBroadcastReceiver;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.network.FirebaseUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 27.01.2017.
 */

public class Notifier {

    public static void setAlarms(String taskId, ArrayList<Remind> reminds) {
        Intent notificationIntent = new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class);
        for (Remind remind : reminds) {
            if (remind.getTimeStamp() > System.currentTimeMillis()) {
                notificationIntent.putExtra(NotificationsBroadcastReceiver.TASK_ID, taskId);
                Log.i("remind", "putting remind with id:" + remind.getId());
                notificationIntent.putExtra(NotificationsBroadcastReceiver.REMINDER_ID, remind.getId());
                notificationIntent.putExtra(NotificationsBroadcastReceiver.DAY, remind.getCalendar().get(Calendar.DAY_OF_YEAR));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(FireTasksApp.getInstance(), (int) remind.getTimeStamp(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, remind.round(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, remind.round(), pendingIntent);
                }
            } else {
                FirebaseUtils.getInstance().removeReminder(remind.getCalendar().get(Calendar.DAY_OF_YEAR), taskId, remind.getId());
            }
        }
    }


    public static void removeAlarm(int code) {
        AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(FireTasksApp.getInstance(), code, new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }
}


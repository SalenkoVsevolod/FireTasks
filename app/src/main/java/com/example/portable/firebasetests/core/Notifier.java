package com.example.portable.firebasetests.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.portable.firebasetests.NotificationsBroadcastReceiver;
import com.example.portable.firebasetests.model.Task;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 27.01.2017.
 */

public class Notifier {

    public static void setAlarm(Task task) {
        Intent notificationIntent = new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class);
        notificationIntent.putExtra(NotificationsBroadcastReceiver.TASK_TAG, task);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(FireTasksApp.getInstance(), (int) task.getTimeStamp(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getTimeStamp());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


    public static void removeAlarm(int requestCode) {
        AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(FireTasksApp.getInstance(), requestCode, new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }
}

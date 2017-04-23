package com.example.portable.firebasetests.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.portable.firebasetests.NotificationsBroadcastReceiver;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseObserver;
import com.example.portable.firebasetests.network.FirebaseUtils;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 27.01.2017.
 */

public class Notifier {

    public static void setAlarms(Task task) {
        Intent notificationIntent = new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class);
        for (String remindId : task.getReminds()) {
            Remind r = FirebaseObserver.getInstance().getReminders().getById(remindId);
            if (r.getTimeStamp() > System.currentTimeMillis()) {
                notificationIntent.putExtra(NotificationsBroadcastReceiver.TASK_ID, task.getId());
                notificationIntent.putExtra(NotificationsBroadcastReceiver.REMINDER_ID, remindId);
                notificationIntent.putExtra(NotificationsBroadcastReceiver.DAY, task.getCalendar().get(Calendar.DAY_OF_YEAR));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(FireTasksApp.getInstance(), (int) r.getTimeStamp(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, r.round(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, r.round(), pendingIntent);
                }
            } else {
                FirebaseUtils.getInstance().removeReminder(task.getCalendar().get(Calendar.DAY_OF_YEAR), task.getId(), r.getId());
            }
        }
    }


    public static void removeAlarm(int code) {
        AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(FireTasksApp.getInstance(), code, new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }
}


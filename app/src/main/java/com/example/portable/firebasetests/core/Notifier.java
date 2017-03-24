package com.example.portable.firebasetests.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.portable.firebasetests.NotificationsBroadcastReceiver;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseManager;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 27.01.2017.
 */

public class Notifier {

    public static void setAlarms(Task task) {
        Intent notificationIntent = new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class);
        notificationIntent.putExtra(NotificationsBroadcastReceiver.TASK_TAG, task);
        for (int i = 0; i < task.getReminds().size(); i++) {
            if (task.getReminds().get(i).getTimeStamp() > System.currentTimeMillis()) {
                notificationIntent.putExtra(NotificationsBroadcastReceiver.INDEX, i);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(FireTasksApp.getInstance(), (int) task.getReminds().get(i).getTimeStamp(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, task.getReminds().get(i).round(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, task.getReminds().get(i).round(), pendingIntent);
                }
            } else {
                FirebaseManager.getInstance().removeReminder(task.getCalendar().get(Calendar.WEEK_OF_YEAR), task.getId(), task.getReminds().get(i));
            }
        }

    }


    public static void removeAlarms(Task task) {
        AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
        for (Remind r : task.getReminds()) {
            alarmManager.cancel(PendingIntent.getBroadcast(FireTasksApp.getInstance(), (int) r.getTimeStamp(), new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));
        }

    }
}

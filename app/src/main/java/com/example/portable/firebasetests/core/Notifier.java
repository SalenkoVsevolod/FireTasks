package com.example.portable.firebasetests.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.portable.firebasetests.NotificationsBroadcastReceiver;
import com.example.portable.firebasetests.model.Task;

/**
 * Created by Salenko Vsevolod on 27.01.2017.
 */

public class Notifier {

    public static void setAlarms(Task task) {
        Intent notificationIntent = new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class);
        notificationIntent.putExtra(NotificationsBroadcastReceiver.TASK_TAG, task);
  /*TODO      for (int i = 0; i < task.getReminds().size(); i++) {
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
                FirebaseUtils.getInstance().removeReminder(task.getCalendar().get(Calendar.DAY_OF_YEAR), task.getId(), task.getReminds().get(i));
            }
        }
*/
    }


    public static void removeAlarm(int code) {
        AlarmManager alarmManager = (AlarmManager) FireTasksApp.getInstance().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(FireTasksApp.getInstance(), code, new Intent(FireTasksApp.getInstance(), NotificationsBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }
}


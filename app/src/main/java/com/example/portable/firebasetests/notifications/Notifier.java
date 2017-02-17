package com.example.portable.firebasetests.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.portable.firebasetests.TagsColors;
import com.example.portable.firebasetests.model.Task;

import java.util.Calendar;

/**
 * Created by Salenko Vsevolod on 27.01.2017.
 */

public class Notifier {

    public static void setAlarm(Task task, Context context) {
        Intent notificationIntent = new Intent(context, NotificationsBroadcastReceiver.class);
        notificationIntent.putExtra(NotificationsBroadcastReceiver.TITLE_TAG, TagsColors.getTags().get((int) task.getTagIndex()).getName());
        notificationIntent.putExtra(NotificationsBroadcastReceiver.TEXT_TAG, task.getDescription());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) task.getTimeStamp(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTimeInMillis(task.getTimeStamp());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }


    public static void removeAlarm(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(context, requestCode, new Intent(context, NotificationsBroadcastReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }
}

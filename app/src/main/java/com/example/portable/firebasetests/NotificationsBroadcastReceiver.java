package com.example.portable.firebasetests;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseObserver;
import com.example.portable.firebasetests.ui.activities.TaskDisplayActivity;

import java.util.Calendar;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    public static final String DAY = "day", TASK_ID = "task_id", REMINDER_ID = "reminder_id";

    public NotificationsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String taskId = intent.getStringExtra(TASK_ID);
        String reminderId = intent.getStringExtra(REMINDER_ID);
        int day = intent.getIntExtra(DAY, -1);
        Task task = FirebaseObserver.getInstance().getTasksDay(day).getById(taskId);
        Log.i("remind", "received remind id:" + reminderId);
        for (Remind r : FirebaseObserver.getInstance().getReminders()) {
            Log.i("remind", "id in storage:" + r.getId());
        }
        Remind remind = FirebaseObserver.getInstance().getReminders().getById(reminderId);
        showPopUp(context, task, remind);
    }

    private void showPopUp(Context context, Task task, Remind remind) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        String sound = remind.getSound();
        if (sound != null) {
            builder.setSound(Uri.parse(sound));
        }
        if (remind.isVibro()) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        Intent intent = new Intent(context, TaskDisplayActivity.class);
        intent.putExtra(TaskDisplayActivity.TASK_ID, task.getId());
        intent.putExtra(TaskDisplayActivity.DAY, task.getCalendar().get(Calendar.DAY_OF_YEAR));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(task.getName());
        builder.setContentText(task.getDescription());
        manager.notify(1, builder.build());
        //TODO FirebaseUtils.getInstance().removeReminder(task.getCalendar().get(Calendar.DAY_OF_YEAR), task.getId(), remind.getId());
    }

}
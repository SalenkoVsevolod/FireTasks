package com.example.portable.firebasetests;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.example.portable.firebasetests.ui.activities.TaskCreateActivity;

import java.util.Calendar;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    public static final String TASK_TAG = "task", INDEX = "index";

    public NotificationsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Task task = (Task) intent.getSerializableExtra(TASK_TAG);
        int index = intent.getIntExtra(INDEX, -1);
        mNotificationManager.notify(1, buildNotification(task, index, context));
        FirebaseManager.getInstance().removeReminder(task.getCalendar().get(Calendar.WEEK_OF_YEAR), task.getId(), task.getReminds().get(index));
    }

    private Notification buildNotification(Task task, int index, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //TODO builder.setContentTitle(TagsColors.getTags().get((int) task.getTagIndex()).getName() + " task");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText(task.getName());
        Log.i("snd", task.getReminds().toString() + ":" + task.getReminds().get(index).getSound());
        String sound = task.getReminds().get(index).getSound();
        if (sound != null) {
            builder.setSound(Uri.parse(sound));
        }
        if (task.getReminds().get(index).isVibro()) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        Intent intent = new Intent(context, TaskCreateActivity.class);
        intent.putExtra(TaskCreateActivity.TASK_ARG, task);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}
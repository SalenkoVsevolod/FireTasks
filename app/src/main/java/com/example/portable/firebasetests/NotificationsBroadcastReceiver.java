package com.example.portable.firebasetests;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.ui.activities.TaskCreateActivity;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    public static final String TASK_TAG = "task";

    public NotificationsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Task task = (Task) intent.getSerializableExtra(TASK_TAG);
        mNotificationManager.notify(1, buildNotification(task, context));
    }

    private Notification buildNotification(Task task, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(TagsColors.getTags().get((int) task.getTagIndex()).getName() + " task");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText(task.getName());
        Intent intent = new Intent(context, TaskCreateActivity.class);
        intent.putExtra(TaskCreateActivity.TASK_ARG, task);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}
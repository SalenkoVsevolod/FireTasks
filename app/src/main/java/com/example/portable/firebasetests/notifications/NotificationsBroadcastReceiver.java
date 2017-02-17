package com.example.portable.firebasetests.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.activities.TasksActivity;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    public static final String TITLE_TAG = "title", TEXT_TAG = "text";
    public NotificationsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String title = intent.getStringExtra(TITLE_TAG), text = intent.getStringExtra(TEXT_TAG);
        mNotificationManager.notify(1, buildNotification(title, text, context));
    }

    private Notification buildNotification(String title, String text, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title + " task");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentText(text);
        Intent intent = new Intent(context, TasksActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}
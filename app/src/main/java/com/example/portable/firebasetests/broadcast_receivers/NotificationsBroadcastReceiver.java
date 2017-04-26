package com.example.portable.firebasetests.broadcast_receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.ui.activities.TaskDisplayActivity;

import java.util.Calendar;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    public static final String REMIND = "remind";

    public NotificationsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Remind remind = (Remind) intent.getSerializableExtra(REMIND);
        showPopUp(context, remind);
    }

    private void showPopUp(Context context, Remind remind) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        String sound = remind.getSound();
        if (sound != null) {
            builder.setSound(Uri.parse(sound));
        }
        if (remind.isVibro()) {
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        Intent intent = new Intent(context, TaskDisplayActivity.class);
        intent.putExtra(TaskDisplayActivity.TASK_ID, remind.getTaskId());
        intent.putExtra(TaskDisplayActivity.DAY, remind.getCalendar().get(Calendar.DAY_OF_YEAR));
        intent.putExtra(TaskDisplayActivity.REMINDER_TO_DELETE, remind.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) remind.getTimeStamp(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(remind.getTitle());
        builder.setContentText(remind.getMessage());
        manager.notify(1, builder.build());
    }
}
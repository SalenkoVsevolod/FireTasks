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
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.ui.activities.TaskDisplayActivity;

import java.util.Calendar;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {
    public static final String DAY = "day", TASK = "task", REMINDER = "reminder";

    public NotificationsBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Task task = (Task) intent.getSerializableExtra(TASK);
        Remind remind = (Remind) intent.getSerializableExtra(REMINDER);
        showPopUp(context, task, remind);
    }

    private void showPopUp(Context context, Task task, Remind remind) {
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
        intent.putExtra(TaskDisplayActivity.TASK_ID, task.getId());
        intent.putExtra(TaskDisplayActivity.DAY, task.getCalendar().get(Calendar.DAY_OF_YEAR));
        Log.i("remind", "sending remind " + remind.getId());
        intent.putExtra(TaskDisplayActivity.REMINDER_TO_DELETE, remind.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) remind.getTimeStamp(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(task.getName());
        builder.setContentText(task.getDescription());
        manager.notify(1, builder.build());
    }
}
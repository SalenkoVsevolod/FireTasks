package com.example.portable.firebasetests;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.portable.firebasetests.model.Task;

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
        showNotification(mNotificationManager, task, index, context);
        //TODO    FirebaseUtils.getInstance().removeReminder(task.getCalendar().get(Calendar.DAY_OF_YEAR), task.getId(), task.getReminds().get(index));
    }

    private void showNotification(final NotificationManager manager, Task task, int index, Context context) {
     /* TODO   final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
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
        Intent intent = new Intent(context, TaskDisplayActivity.class);
        intent.putExtra(TaskDisplayActivity.TASK_ARG, task);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(task.getName());
        builder.setContentText(task.getDescription());
        manager.notify(1, builder.build());*/
    }
}
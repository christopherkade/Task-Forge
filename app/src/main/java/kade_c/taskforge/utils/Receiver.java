package kade_c.taskforge.utils;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;

/**
 * Handles notifications
 */
public class Receiver extends BroadcastReceiver {

    Activity activity;

    public void onReceive(Context context, Intent intent) {
        Intent intent_ = new Intent(activity, TaskForgeActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(activity, 0, intent_, 0);

        Notification n  = new Notification.Builder(activity)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(activity.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);


    }
}
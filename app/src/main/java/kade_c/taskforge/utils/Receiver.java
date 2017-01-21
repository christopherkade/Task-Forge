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

        //Toast.makeText(context, "Repeating Alarm worked.", Toast.LENGTH_LONG).show();


        // try here

        // prepare intent which is triggered if the
        // notification is selected

        Intent intent_ = new Intent(activity, TaskForgeActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(activity, 0, intent_, 0);

        // build notification
        // the addAction re-use the same intent to keep the example short
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

//    @Override
//    public void onCreate() {
//        Intent intent = new Intent(this, TaskForgeActivity.class);
//        long[] pattern = {0, 300, 0};
//        PendingIntent pi = PendingIntent.getActivity(this, 01234, intent, 0);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("TaskForge event")
//                .setContentText("Don't forget your task !")
//                .setVibrate(pattern)
//                .setAutoCancel(true);
//
//        mBuilder.setContentIntent(pi);
//        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
//        mBuilder.setAutoCancel(true);
//        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(01234, mBuilder.build());
//    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, intent.getStringExtra("param"),Toast.LENGTH_SHORT).show();
//    }

//    public void sendNotification(String eventDate) {
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            Date date = dateFormat.parse(eventDate);
//
//            AlarmManager alarms = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
//
//            Receiver receiver = new Receiver();
//            IntentFilter filter = new IntentFilter("ALARM_ACTION");
//            activity.registerReceiver(receiver, filter);
//
//            Intent intent = new Intent("ALARM_ACTION");
//            intent.putExtra("param", "My scheduled action");
//            PendingIntent operation = PendingIntent.getBroadcast(activity, 0, intent, 0);
//
//            long secs = (date.getTime())/1000;
//
//            alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+secs, operation) ;
//        } catch (ParseException e)  {
//            e.printStackTrace();
//        }
//
//    }

}
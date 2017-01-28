package kade_c.taskforge.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;
import kade_c.taskforge.fragments.SettingsFragment;

/**
 * Handles notifications
 */
public class NotificationHandler extends BroadcastReceiver {
    private final String NOTIFICATION_ID = "notification-id";
    private final String NOTIFICATION = "notification";

    /**
     * Called when a notification must be sent to user
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);
    }

    /**
     * Schedules a notification at the given delay
     */
    private void scheduleNotification(Notification notification, long delay, Activity activity) {
        Intent notificationIntent = new Intent(activity, NotificationHandler.class);
        notificationIntent.putExtra(NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    /**
     * Builds a notification with the given title and content
     */
    private Notification getNotification(String title, String content, Activity activity, String tab) {
        Notification.Builder builder = new Notification.Builder(activity);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(activity, TaskForgeActivity.class);
        resultIntent.putExtra("previousTab", tab);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addParentStack(TaskForgeActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        return builder.build();
    }


    /**
     * Sets a notification to be displayed at the given time and date.
     */
    public void setNotification(String title, String content, String date, String time, Activity activity, String tab) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

            boolean allowNotifictions = sharedPref.getBoolean(SettingsFragment.KEY_PREF_NOTIFICATIONS, true);

            if (allowNotifictions) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
                Date fullDate = sdf.parse(date + " " + time);

                long ms = fullDate.getTime() - System.currentTimeMillis();

                scheduleNotification(getNotification(title, content, activity, tab), ms, activity);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

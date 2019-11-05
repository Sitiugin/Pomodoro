package com.glebworx.pomodoro.util.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.activity.MainActivity;
import com.glebworx.pomodoro.util.IdGenerator;

import java.util.Objects;

public class TaskNotificationManager {

    private static final String CHANNEL_ID = "notification_channel_pomodoro";

    private Context context;
    private NotificationManagerCompat notificationManager;
    private int notificationId;

    public TaskNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
    }

    public static void createNotificationChannel(Context context) {
        CharSequence name = context.getString(R.string.notification_channel_name);
        String description = context.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        //channel.setSound(); // TODO
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
    }

    public int showPersistentNotification(String taskName, String status) {
        if (notificationManager.areNotificationsEnabled()) {
            notificationId = IdGenerator.next();
            Notification notification = getNotificationBuilder(taskName, status).build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(notificationId, notification);
            return notificationId;
        }
        return 0;
    }

    public void cancelNotification() {
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.cancel(notificationId);
        }
    }

    public void cancelNotification(int notificationId) {
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.cancel(notificationId);
        }
    }

    public void cancelAllNotifications() {
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.cancelAll();
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String taskName, String status) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer_black)
                .setContentTitle(taskName)
                .setContentText(status)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(status))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(getIntent())
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setProgress(100, 50, false)
                .addAction(
                        R.drawable.ic_pause_highlight,
                        context.getString(R.string.bottom_sheet_text_status_paused),
                        getPauseIntent());
    }

    private PendingIntent getIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT); // TODO figure out flags
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private PendingIntent getPauseIntent() { // TODO implement
        Intent intent = new Intent(context, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private PendingIntent getResumeIntent() { // TODO implement
        Intent intent = new Intent(context, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

}

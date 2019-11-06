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
import com.glebworx.pomodoro.util.NotificationReceiver;

import java.util.Objects;

import static com.glebworx.pomodoro.util.NotificationReceiver.ACTION_PAUSE;
import static com.glebworx.pomodoro.util.NotificationReceiver.ACTION_RESUME;
import static com.glebworx.pomodoro.util.NotificationReceiver.ACTION_START;
import static com.glebworx.pomodoro.util.NotificationReceiver.KEY_ACTION;

public class TaskNotificationManager {

    public static final String STATUS_READY = "status_ready";
    public static final String STATUS_WORKING = "status_working";
    public static final String STATUS_PAUSED = "status_paused";

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
            NotificationCompat.Builder builder = getNotificationBuilder();
            setContent(builder, taskName, status);
            addAction(builder, status);
            notify(builder, notificationId);
            return notificationId;
        }
        return 0;
    }

    public void updateNotification(String taskName,
                                   String status,
                                   int progress) {
        if (notificationManager.areNotificationsEnabled()) {
            NotificationCompat.Builder builder = getNotificationBuilder();
            setContent(builder, taskName, status);
            setProgress(builder, progress);
            addAction(builder, status);
            notify(builder, notificationId);
        }
    }

    public void updateNotification(String taskName,
                                   int progress) {
        if (notificationManager.areNotificationsEnabled()) {
            NotificationCompat.Builder builder = getNotificationBuilder();
            setContent(builder, taskName, STATUS_WORKING);
            setProgress(builder, progress);
            addAction(builder, STATUS_WORKING);
            notify(builder, notificationId);
        }
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

    private NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer_gray_small)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(getIntent())
                .setAutoCancel(false)
                .setOnlyAlertOnce(true);
    }

    private void notify(NotificationCompat.Builder builder, int notificationId) {
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notificationId, notification);
    }

    private void setContent(NotificationCompat.Builder builder, String taskName, String status) {
        builder.setContentTitle(taskName);
        switch (status) {
            case STATUS_READY:
                builder.setContentText(context.getString(R.string.notification_title_ready));
                break;
            case STATUS_WORKING:
                builder.setContentText(context.getString(R.string.notification_title_working));
                break;
            case STATUS_PAUSED:
                builder.setContentText(context.getString(R.string.notification_title_paused));
                break;
        }
    }

    private void setProgress(NotificationCompat.Builder builder, int progress) {
        builder.setProgress(100, progress, false);
    }

    private void addAction(NotificationCompat.Builder builder, String status) {
        switch (status) {
            case STATUS_READY:
                addStartAction(builder);
                break;
            case STATUS_WORKING:
                addPauseAction(builder);
                break;
            case STATUS_PAUSED:
                addResumeAction(builder);
                break;
        }
    }

    private void addStartAction(NotificationCompat.Builder builder) {
        builder.addAction(
                R.drawable.ic_play_highlight,
                context.getString(R.string.notification_title_start),
                getActionIntent(ACTION_START));
    }

    private void addPauseAction(NotificationCompat.Builder builder) {
        builder.addAction(
                R.drawable.ic_pause_highlight,
                context.getString(R.string.notification_title_pause),
                getActionIntent(ACTION_PAUSE));
    }

    private void addResumeAction(NotificationCompat.Builder builder) {
        builder.addAction(
                R.drawable.ic_play_highlight,
                context.getString(R.string.notification_title_resume),
                getActionIntent(ACTION_RESUME));
    }

    private PendingIntent getIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

    private PendingIntent getActionIntent(String action) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(KEY_ACTION, action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

}

package com.glebworx.pomodoro.util.manager;

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

    public TaskNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = context.getString(R.string.notification_channel_name);
        String description = context.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
    }

    public void showPersistentNotification() {
        notificationManager.notify(IdGenerator.next(), getNotificationBuilder().build());
        /*Notification not = new Notification(R.drawable.icon, "Application started", System.currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, main.class), Notification.FLAG_ONGOING_EVENT);
        not.flags = Notification.FLAG_ONGOING_EVENT;
        not.setLatestEventInfo(this, "Application Name", "Application Description", contentIntent);
        mNotificationManager.notify(1, not);*/
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_timer_black)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(getIntent())
                .setAutoCancel(false);
    }

    private PendingIntent getIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

}

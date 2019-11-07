package com.glebworx.pomodoro.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String KEY_ACTION = "action";

    public static final String ACTION_START = "ACTION_START_TASK";
    public static final String ACTION_PAUSE = "ACTION_PAUSE_TASK";
    public static final String ACTION_RESUME = "ACTION_RESUME_TASK";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ACTION CLICKED", Toast.LENGTH_LONG).show();
        String action = intent.getStringExtra(KEY_ACTION);
        if (action == null) {
            return;
        }
        switch (action) {
            case ACTION_START:
                break;
            case ACTION_PAUSE:
                break;
            case ACTION_RESUME:
                break;
        }
    }

}

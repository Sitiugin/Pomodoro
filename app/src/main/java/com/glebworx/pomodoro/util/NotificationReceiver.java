package com.glebworx.pomodoro.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String KEY_ACTION = "action";

    public static final String ACTION_START = "action_start";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_RESUME = "action_resume";

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "ACTION CLICKED", Toast.LENGTH_LONG).show();
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

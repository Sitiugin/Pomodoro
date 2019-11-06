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

    }

}

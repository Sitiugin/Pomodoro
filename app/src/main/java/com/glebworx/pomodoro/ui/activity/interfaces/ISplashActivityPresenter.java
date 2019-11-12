package com.glebworx.pomodoro.ui.activity.interfaces;

import android.content.Context;
import android.content.Intent;

public interface ISplashActivityPresenter {

    void init();

    void handleIntent(Intent intent, Context context);

    void sendSignInLink(String email, boolean isRepeat, Context context);

    void openEmailClient(Context context);
}

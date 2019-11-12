package com.glebworx.pomodoro.ui.activity.interfaces;

import android.content.Context;
import android.content.Intent;

public interface ISplashActivityPresenter {

    void init();

    void handleIntent(Intent intent, Context context);

    void sendSignInLink(String email, Context context);

    void openEmailClient(Context context);
}

package com.glebworx.pomodoro.ui.activity;

import com.glebworx.pomodoro.ui.activity.interfaces.ISplashActivity;
import com.glebworx.pomodoro.ui.activity.interfaces.ISplashActivityPresenter;

public class SplashActivityPresenter implements ISplashActivityPresenter {

    private ISplashActivity presenterListener;

    public SplashActivityPresenter(ISplashActivity presenterListener) {
        this.presenterListener = presenterListener;
    }
}

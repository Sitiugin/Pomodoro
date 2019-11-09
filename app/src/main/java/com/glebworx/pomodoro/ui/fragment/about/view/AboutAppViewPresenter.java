package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutAppView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutAppViewPresenter;

public class AboutAppViewPresenter implements IAboutAppViewPresenter {

    private IAboutAppView presenterListener;

    AboutAppViewPresenter(@NonNull IAboutAppView presenterListener) {
        this.presenterListener = presenterListener;
        init(null);
    }

    @Override
    public void init(Context context) {

    }

}

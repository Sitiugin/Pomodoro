package com.glebworx.pomodoro.ui.fragment.about.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutViewPresenter;

public class AboutPrivacyViewPresenter implements IAboutViewPresenter {

    private IAboutView presenterListener;

    public AboutPrivacyViewPresenter(@NonNull IAboutView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {

    }

}

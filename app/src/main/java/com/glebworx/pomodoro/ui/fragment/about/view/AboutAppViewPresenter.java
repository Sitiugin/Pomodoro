package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutViewPresenter;

public class AboutAppViewPresenter implements IAboutViewPresenter {

    private IAboutView presenterListener;

    public AboutAppViewPresenter(@NonNull IAboutView presenterListener) {
        this.presenterListener = presenterListener;
        init(null);
    }

    @Override
    public void init(Context context) {

    }

}

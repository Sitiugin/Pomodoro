package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutTermsView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutTermsViewPresenter;

public class AboutTermsViewPresenter implements IAboutTermsViewPresenter {

    private IAboutTermsView presenterListener;

    AboutTermsViewPresenter(@NonNull IAboutTermsView presenterListener, boolean isEmbedded) {
        this.presenterListener = presenterListener;
        init(null, isEmbedded);
    }

    @Override
    public void init(Context context, boolean isEmbedded) {

    }

}

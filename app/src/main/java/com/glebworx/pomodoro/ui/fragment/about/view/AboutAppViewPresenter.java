package com.glebworx.pomodoro.ui.fragment.about.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.ArrayList;
import java.util.List;

public class AboutAppViewPresenter implements IAboutViewPresenter {

    private IAboutView presenterListener;

    public AboutAppViewPresenter(@NonNull IAboutView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        List<AboutItem> items = new ArrayList<>();
        items.add(new AboutItem("AAA", "asdawrqr"));
        items.add(new AboutItem("A1AA", "asdawrqr"));
        presenterListener.onInitView(items);
    }

}

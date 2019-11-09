package com.glebworx.pomodoro.ui.fragment.about.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutLicensesView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutLicenseItem;

import java.util.ArrayList;
import java.util.List;

public class AboutLicensesViewPresenter implements IAboutViewPresenter {

    private IAboutLicensesView presenterListener;

    public AboutLicensesViewPresenter(@NonNull IAboutLicensesView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        List<AboutLicenseItem> items = new ArrayList<>();
        items.add(new AboutLicenseItem("AS", "asdawrqr", "ASD"));
        items.add(new AboutLicenseItem("A1AA", "asdawrqr", "ASD"));
        presenterListener.onInitView(items);
    }

}

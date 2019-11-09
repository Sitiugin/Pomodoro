package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutLicensesView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutLicenseItem;

import java.util.ArrayList;
import java.util.List;

public class AboutLicensesViewPresenter implements IAboutViewPresenter {

    private IAboutLicensesView presenterListener;

    public AboutLicensesViewPresenter(@NonNull IAboutLicensesView presenterListener, Context context) {
        this.presenterListener = presenterListener;
        init(context);
    }

    @Override
    public void init(Context context) {

        List<AboutLicenseItem> items = new ArrayList<>();

        items.add(getItem(
                context,
                R.string.about_licenses_title_apache_2,
                R.string.about_licenses_text_apache_2,
                R.string.about_licenses_text_apache_2_libraries));

        items.add(getItem(
                context,
                R.string.about_licenses_title_mit,
                R.string.about_licenses_text_mit,
                R.string.about_licenses_text_mit_libraries));

        presenterListener.onInitView(items);

    }

    private AboutLicenseItem getItem(
            Context context,
            int titleId,
            int textId,
            int librariesId) {
        return new AboutLicenseItem(
                context.getString(titleId),
                context.getString(textId),
                context.getString(librariesId));
    }

}

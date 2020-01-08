package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutLicensesView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutLicensesViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutLicenseItem;

import java.util.ArrayList;
import java.util.List;

public class AboutLicensesViewPresenter implements IAboutLicensesViewPresenter {

    private IAboutLicensesView presenterListener;

    AboutLicensesViewPresenter(@NonNull IAboutLicensesView presenterListener) {
        this.presenterListener = presenterListener;
    }

    @Override
    public void init(Context context) {

        List<AboutLicenseItem> items = new ArrayList<>();

        items.add(getItem(
                context,
                R.string.about_licenses_title_apache_2,
                R.string.about_licenses_text_apache_2,
                R.string.about_licenses_text_apache_2_libraries,
                R.string.about_licenses_text_apache_2_uri));

        items.add(getItem(
                context,
                R.string.about_licenses_title_mit,
                R.string.about_licenses_text_mit,
                R.string.about_licenses_text_mit_libraries,
                R.string.about_licenses_text_mit_uri));

        items.add(getItem(
                context,
                R.string.about_licenses_title_isc,
                R.string.about_licenses_text_isc,
                R.string.about_licenses_text_isc_libraries,
                R.string.about_licenses_text_isc_uri));

        items.add(getSimpleItem(
                context,
                R.string.about_licenses_title_firebase,
                R.string.about_licenses_text_firebase_uri));

        items.add(getSimpleItem(
                context,
                R.string.about_licenses_title_linear_icons,
                R.string.about_licenses_text_linear_icons_uri));

        presenterListener.onInitView(items);

    }

    private AboutLicenseItem getSimpleItem(
            Context context,
            int titleId,
            int textId) {
        String uri = context.getString(textId);
        return new AboutLicenseItem(
                context.getString(titleId),
                uri,
                null,
                uri);
    }

    private AboutLicenseItem getItem(
            Context context,
            int titleId,
            int textId,
            int librariesId,
            int uriId) {
        return new AboutLicenseItem(
                context.getString(titleId),
                context.getString(textId),
                context.getString(librariesId),
                context.getString(uriId));
    }

}

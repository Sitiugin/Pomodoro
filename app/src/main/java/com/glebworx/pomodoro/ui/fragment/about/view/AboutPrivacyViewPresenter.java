package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.ArrayList;
import java.util.List;

public class AboutPrivacyViewPresenter implements IAboutViewPresenter {

    private IAboutView presenterListener;

    AboutPrivacyViewPresenter(@NonNull IAboutView presenterListener, Context context) {
        this.presenterListener = presenterListener;
        init(context);
    }

    @Override
    public void init(Context context) {

        List<AboutItem> items = new ArrayList<>();

        items.add(getItem(
                context,
                R.string.about_privacy_text_intro));
        items.add(getItem(
                context,
                R.string.about_privacy_title_information_collection_and_use,
                R.string.about_privacy_text_information_collection_and_use));
        items.add(getItem(
                context,
                R.string.about_privacy_title_log_data,
                R.string.about_privacy_text_log_data));
        items.add(getItem(
                context,
                R.string.about_privacy_title_cookies,
                R.string.about_privacy_text_cookies));
        items.add(getItem(
                context,
                R.string.about_privacy_title_service_providers,
                R.string.about_privacy_text_service_providers));
        items.add(getItem(
                context,
                R.string.about_privacy_title_security,
                R.string.about_privacy_text_security));
        items.add(getItem(
                context,
                R.string.about_privacy_title_links_to_other_sites,
                R.string.about_privacy_text_links_to_other_sites));
        items.add(getItem(
                context,
                R.string.about_privacy_title_childrens_privacy,
                R.string.about_privacy_text_childrens_privacy));
        items.add(getItem(
                context,
                R.string.about_privacy_title_changes_to_this_policy,
                R.string.about_privacy_text_changes_to_this_policy));
        items.add(getItem(
                context,
                R.string.about_privacy_title_contact_us,
                R.string.about_privacy_text_contact_us));

        presenterListener.onInitView(items);

    }

    private AboutItem getItem(Context context, int textId) {
        return new AboutItem(context.getString(textId));
    }

    private AboutItem getItem(Context context, int titleId, int textId) {
        return new AboutItem(context.getString(titleId), context.getString(textId));
    }

}

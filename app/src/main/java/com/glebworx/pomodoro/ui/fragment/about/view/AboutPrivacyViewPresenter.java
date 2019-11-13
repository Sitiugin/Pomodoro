package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutPrivacyView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutPrivacyViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutEmbeddedItem;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.ArrayList;
import java.util.List;

public class AboutPrivacyViewPresenter implements IAboutPrivacyViewPresenter {

    private IAboutPrivacyView presenterListener;

    AboutPrivacyViewPresenter(@NonNull IAboutPrivacyView presenterListener, Context context, boolean isEmbedded) {
        this.presenterListener = presenterListener;
        init(context, isEmbedded);
    }

    @Override
    public void init(Context context, boolean isEmbedded) {
        List<AboutItem> items = new ArrayList<>();
        if (isEmbedded) {
            addEmbeddedItems(context, items);
        } else {
            addItems(context, items);
        }
        presenterListener.onInitView(items);
    }

    private void addEmbeddedItems(Context context, List<AboutItem> items) {
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_text_intro));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_information_collection_and_use,
                R.string.about_privacy_text_information_collection_and_use));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_log_data,
                R.string.about_privacy_text_log_data));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_cookies,
                R.string.about_privacy_text_cookies));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_service_providers,
                R.string.about_privacy_text_service_providers));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_security,
                R.string.about_privacy_text_security));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_links_to_other_sites,
                R.string.about_privacy_text_links_to_other_sites));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_childrens_privacy,
                R.string.about_privacy_text_childrens_privacy));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_changes_to_this_policy,
                R.string.about_privacy_text_changes_to_this_policy));
        items.add(getEmbeddedItem(
                context,
                R.string.about_privacy_title_contact_us,
                R.string.about_privacy_text_contact_us));
    }

    private void addItems(Context context, List<AboutItem> items) {
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
    }

    private AboutItem getEmbeddedItem(Context context, int textId) {
        return new AboutEmbeddedItem(context.getString(textId));
    }

    private AboutItem getEmbeddedItem(Context context, int titleId, int textId) {
        return new AboutEmbeddedItem(context.getString(titleId), context.getString(textId));
    }

    private AboutItem getItem(Context context, int textId) {
        return new AboutItem(context.getString(textId));
    }

    private AboutItem getItem(Context context, int titleId, int textId) {
        return new AboutItem(context.getString(titleId), context.getString(textId));
    }

}

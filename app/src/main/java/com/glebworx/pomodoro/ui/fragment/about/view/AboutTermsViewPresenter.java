package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutTermsView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutTermsViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutEmbeddedItem;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.ArrayList;
import java.util.List;

public class AboutTermsViewPresenter implements IAboutTermsViewPresenter {

    private IAboutTermsView presenterListener;

    AboutTermsViewPresenter(@NonNull IAboutTermsView presenterListener, Context context, boolean isEmbedded) {
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
                R.string.about_terms_title_overview,
                R.string.about_terms_text_overview));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_general_conditions,
                R.string.about_terms_text_general_conditions));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_accuracy_completeness_and_timeliness_of_information,
                R.string.about_terms_text_accuracy_completeness_and_timeliness_of_information));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_modifications_to_the_service_and_prices,
                R.string.about_terms_text_modifications_to_the_service_and_prices));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_personal_information,
                R.string.about_terms_text_personal_information));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_prohibited_uses,
                R.string.about_terms_text_prohibited_uses));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_disclaimer_of_warranties_limitation_of_liability,
                R.string.about_terms_text_disclaimer_of_warranties_limitation_of_liability));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_indemnification,
                R.string.about_terms_text_indemnification));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_severability,
                R.string.about_terms_text_severability));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_termination,
                R.string.about_terms_text_termination));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_entire_agreement,
                R.string.about_terms_text_entire_agreement));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_governing_law,
                R.string.about_terms_text_governing_law));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_changes_to_terms_of_service,
                R.string.about_terms_text_changes_to_terms_of_service));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_contact_information,
                R.string.about_terms_text_contact_information));
    }

    private void addItems(Context context, List<AboutItem> items) {
        items.add(getItem(
                context,
                R.string.about_terms_title_overview,
                R.string.about_terms_text_overview));
        items.add(getItem(
                context,
                R.string.about_terms_title_general_conditions,
                R.string.about_terms_text_general_conditions));
        items.add(getItem(
                context,
                R.string.about_terms_title_accuracy_completeness_and_timeliness_of_information,
                R.string.about_terms_text_accuracy_completeness_and_timeliness_of_information));
        items.add(getItem(
                context,
                R.string.about_terms_title_modifications_to_the_service_and_prices,
                R.string.about_terms_text_modifications_to_the_service_and_prices));
        items.add(getItem(
                context,
                R.string.about_terms_title_personal_information,
                R.string.about_terms_text_personal_information));
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_uses,
                R.string.about_terms_text_prohibited_uses));
        items.add(getItem(
                context,
                R.string.about_terms_title_disclaimer_of_warranties_limitation_of_liability,
                R.string.about_terms_text_disclaimer_of_warranties_limitation_of_liability));
        items.add(getItem(
                context,
                R.string.about_terms_title_indemnification,
                R.string.about_terms_text_indemnification));
        items.add(getItem(
                context,
                R.string.about_terms_title_severability,
                R.string.about_terms_text_severability));
        items.add(getItem(
                context,
                R.string.about_terms_title_termination,
                R.string.about_terms_text_termination));
        items.add(getItem(
                context,
                R.string.about_terms_title_entire_agreement,
                R.string.about_terms_text_entire_agreement));
        items.add(getItem(
                context,
                R.string.about_terms_title_governing_law,
                R.string.about_terms_text_governing_law));
        items.add(getItem(
                context,
                R.string.about_terms_title_changes_to_terms_of_service,
                R.string.about_terms_text_changes_to_terms_of_service));
        items.add(getItem(
                context,
                R.string.about_terms_title_contact_information,
                R.string.about_terms_text_contact_information));
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

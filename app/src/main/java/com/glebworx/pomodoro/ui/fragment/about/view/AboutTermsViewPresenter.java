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
                R.string.about_terms_title_prohibited_uses,
                R.string.about_terms_text_prohibited_uses));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_intellectual_property,
                R.string.about_terms_text_intellectual_property));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_prohibited_user_generated_content,
                R.string.about_terms_text_prohibited_user_generated_content));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_prohibited_copyright_infringement,
                R.string.about_terms_text_prohibited_copyright_infringement));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_prohibited_payment_clauses,
                R.string.about_terms_text_prohibited_payment_clauses));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_prohibited_limitation_of_liability_and_warranties,
                R.string.about_terms_text_prohibited_limitation_of_liability_and_warranties));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_prohibited_right_to_terminate_accounts,
                R.string.about_terms_text_prohibited_right_to_terminate_accounts));
        items.add(getEmbeddedItem(
                context,
                R.string.about_terms_title_prohibited_governing_law_and_jurisdiction,
                R.string.about_terms_text_prohibited_governing_law_and_jurisdiction));
    }

    private void addItems(Context context, List<AboutItem> items) {
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_uses,
                R.string.about_terms_text_prohibited_uses));
        items.add(getItem(
                context,
                R.string.about_terms_title_intellectual_property,
                R.string.about_terms_text_intellectual_property));
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_user_generated_content,
                R.string.about_terms_text_prohibited_user_generated_content));
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_copyright_infringement,
                R.string.about_terms_text_prohibited_copyright_infringement));
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_payment_clauses,
                R.string.about_terms_text_prohibited_payment_clauses));
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_limitation_of_liability_and_warranties,
                R.string.about_terms_text_prohibited_limitation_of_liability_and_warranties));
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_right_to_terminate_accounts,
                R.string.about_terms_text_prohibited_right_to_terminate_accounts));
        items.add(getItem(
                context,
                R.string.about_terms_title_prohibited_governing_law_and_jurisdiction,
                R.string.about_terms_text_prohibited_governing_law_and_jurisdiction));
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

package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutAppView;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutAppViewPresenter;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutAppHeaderItem;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.ArrayList;
import java.util.List;

public class AboutAppViewPresenter implements IAboutAppViewPresenter {

    private IAboutAppView presenterListener;

    AboutAppViewPresenter(@NonNull IAboutAppView presenterListener) {
        this.presenterListener = presenterListener;
    }

    @Override
    public void init(Context context) {
        List<AboutItem> items = new ArrayList<>();
        addItems(context, items);
        presenterListener.onInitView(new AboutAppHeaderItem(), items);
    }

    private void addItems(Context context, List<AboutItem> items) {
        items.add(getItem(
                context,
                R.string.about_app_text_description));
        items.add(getItem(
                context,
                R.string.about_app_title_features,
                R.string.about_app_text_features));
    }

    private AboutItem getItem(Context context, int textId) {
        return new AboutItem(context.getString(textId));
    }

    private AboutItem getItem(Context context, int titleId, int textId) {
        return new AboutItem(context.getString(titleId), context.getString(textId));
    }

}

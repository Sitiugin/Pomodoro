package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutView;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.List;

public class AboutAppView extends NestedScrollView implements IAboutView {

    private AboutAppViewPresenter presenter;

    public AboutAppView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public AboutAppView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AboutAppView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void onInitView(List<AboutItem> items) {

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        View rootView = inflate(context, R.layout.view_about_app, this);
        presenter = new AboutAppViewPresenter(this);
    }

}
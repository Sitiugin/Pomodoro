package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutTermsView;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.List;

public class AboutTermsView extends RecyclerView implements IAboutTermsView {

    private AboutTermsViewPresenter presenter;

    public AboutTermsView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public AboutTermsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AboutTermsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void onInitView(List<AboutItem> items) {

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AboutTermsView,
                0, 0);

        boolean isEmbedded;
        try {
            isEmbedded = a.getBoolean(R.styleable.AboutTermsView_embedded, false);
        } finally {
            a.recycle();
        }
        View rootView = inflate(context, R.layout.view_about_terms, this);
        presenter = new AboutTermsViewPresenter(this, isEmbedded);
    }

}

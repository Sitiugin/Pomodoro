package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;

public class AboutPrivacyView extends RecyclerView {

    public AboutPrivacyView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public AboutPrivacyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AboutPrivacyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        View rootView = inflate(context, R.layout.view_about_privacy, this);
    }

}

package com.glebworx.pomodoro.util;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public class ZeroStateDecoration extends RecyclerView.ItemDecoration {

    private final int layoutId;
    private View view;
    private boolean enabled = true;

    public ZeroStateDecoration(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        if (enabled && parent.getChildCount() == 0) {

            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(layoutId, parent, false);
            }

            view.measure(makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY), makeMeasureSpec(parent.getHeight(), View.MeasureSpec.EXACTLY));
            view.layout(parent.getLeft(), parent.getTop(), parent.getRight(), parent.getBottom());
            view.draw(c);

        }

    }

}

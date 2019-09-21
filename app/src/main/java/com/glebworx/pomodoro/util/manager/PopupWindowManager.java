package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;


import androidx.core.widget.PopupWindowCompat;

import com.glebworx.pomodoro.R;

import javax.annotation.Nonnull;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PopupWindowManager {

    private Context context;

    public PopupWindowManager(Context context) {
        this.context = context;
    }

    public PopupWindow showPopup(int layoutId, @Nonnull View anchorView, int gravity) {

        PopupWindow popupWindow = getPopupWindow(layoutId, false);
        if (popupWindow == null) {
            return null;
        }

        PopupWindowCompat.showAsDropDown(popupWindow, anchorView, 0, 0, gravity);

        return popupWindow;

    }

    public PopupWindow getPopupWindow(int layoutId, boolean fullScreen) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        if (layoutInflater == null) {
            return null;
        }

        View popupView = layoutInflater.inflate(layoutId, null);
        PopupWindow popupWindow = fullScreen
            ? new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
            : new PopupWindow(context);

        popupWindow.setContentView(popupView);
        popupWindow.setBackgroundDrawable(context.getDrawable(R.drawable.drawable_foreground));
        popupWindow.setElevation(context.getResources().getDimensionPixelSize(R.dimen.elevation_popup));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        return popupWindow;
    }

}

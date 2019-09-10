package com.glebworx.pomodoro.util.manager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import javax.annotation.Nonnull;

public class DialogManager {

    private DialogManager() { }

    public static AlertDialog showDialog(@Nonnull Activity activity, int rootViewId, int layoutId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(layoutId, activity.findViewById(rootViewId), false);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        return alertDialog;
    }

}

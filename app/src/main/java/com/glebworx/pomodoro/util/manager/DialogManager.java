package com.glebworx.pomodoro.util.manager;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;

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

    public static void showGenericDialog(@NonNull Activity activity,
                                         int rootViewId,
                                         int titleId,
                                         int descriptionId,
                                         int positiveButtonTextId,
                                         IDialogManager callback) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_generic, activity.findViewById(rootViewId), false);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        ((AppCompatTextView) dialogView.findViewById(R.id.text_view_title)).setText(titleId);
        ((AppCompatTextView) dialogView.findViewById(R.id.text_view_description)).setText(descriptionId);

        AppCompatButton positiveButton = dialogView.findViewById(R.id.button_positive);

        positiveButton.setText(positiveButtonTextId);

        View.OnClickListener onClickListener = view -> {
            if (!alertDialog.isShowing()) {
                return;
            }
            if (view.getId() == R.id.button_positive) {
                alertDialog.dismiss();
                callback.onPositiveButtonClicked();
            } else if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            }
        };

        dialogView.findViewById(R.id.button_negative).setOnClickListener(onClickListener);
        positiveButton.setOnClickListener(onClickListener);

        alertDialog.show();

    }

    public interface IDialogManager {
        void onPositiveButtonClicked();
    }

}

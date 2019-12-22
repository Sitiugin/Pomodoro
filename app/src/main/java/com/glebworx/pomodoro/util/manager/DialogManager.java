package com.glebworx.pomodoro.util.manager;

import android.app.Activity;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;

import java.util.Objects;

import javax.annotation.Nonnull;

public class DialogManager {

    private DialogManager() { }

    public static AlertDialog buildDialog(@Nonnull Activity activity, int rootViewId, int layoutId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(layoutId, activity.findViewById(rootViewId), false);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(activity.getDrawable(R.drawable.drawable_foreground_rounded));
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

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(activity.getDrawable(R.drawable.drawable_foreground_rounded));

        ((AppCompatTextView) dialogView.findViewById(R.id.text_view_title)).setText(titleId);
        ((AppCompatTextView) dialogView.findViewById(R.id.text_view_description)).setText(descriptionId);

        AppCompatButton positiveButton = dialogView.findViewById(R.id.button_positive);

        positiveButton.setText(positiveButtonTextId);

        View.OnClickListener onClickListener = getOnClickListener(alertDialog, callback);

        dialogView.findViewById(R.id.button_negative).setOnClickListener(onClickListener);
        positiveButton.setOnClickListener(onClickListener);

        alertDialog.show();

    }

    public static void showGenericDialog(@NonNull Activity activity,
                                         int rootViewId,
                                         int titleId,
                                         Spanned description,
                                         int positiveButtonTextId,
                                         IDialogManager callback) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_generic, activity.findViewById(rootViewId), false);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(activity.getDrawable(R.drawable.drawable_foreground_rounded));

        ((AppCompatTextView) dialogView.findViewById(R.id.text_view_title)).setText(titleId);
        ((AppCompatTextView) dialogView.findViewById(R.id.text_view_description)).setText(description);

        AppCompatButton positiveButton = dialogView.findViewById(R.id.button_positive);

        positiveButton.setText(positiveButtonTextId);

        View.OnClickListener onClickListener = getOnClickListener(alertDialog, callback);

        dialogView.findViewById(R.id.button_negative).setOnClickListener(onClickListener);
        positiveButton.setOnClickListener(onClickListener);

        alertDialog.show();

    }

    private static View.OnClickListener getOnClickListener(AlertDialog alertDialog, IDialogManager callback) {
        return view -> {
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
    }

    public interface IDialogManager {
        void onPositiveButtonClicked();
    }

}

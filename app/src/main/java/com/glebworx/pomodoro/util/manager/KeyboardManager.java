package com.glebworx.pomodoro.util.manager;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import javax.annotation.Nonnull;

public class KeyboardManager {

    private KeyboardManager() { }

    public static void showKeyboard(@NonNull Activity activity, @Nonnull AppCompatEditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

    }

    public static void hideKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

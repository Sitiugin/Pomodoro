package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SharedPrefsManager {

    private static final String PREFS_NAME = "shared_prefs";

    private static final String KEY_EMAIL = "prefs_email";
    private static final String KEY_POMODORO_TARGET = "prefs_pomodoro_target";

    private Context context;

    public SharedPrefsManager(@NonNull Context context) {
        this.context = context;
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public int getPomodoroTarget() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getInt(KEY_POMODORO_TARGET, 0);
    }

    public void setPomodoroTarget(int target) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(KEY_POMODORO_TARGET, target);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}

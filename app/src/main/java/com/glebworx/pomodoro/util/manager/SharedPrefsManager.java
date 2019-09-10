package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SharedPrefsManager {

    private static final String PREFS_NAME = "shared_prefs";

    private static final String KEY_EMAIL = "prefs_email";
    private static final String KEY_UNITS = "prefs_units";

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

    public void setUnits(boolean isKg) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(KEY_UNITS, isKg);
        editor.apply();
    }

    public boolean getUnits() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getBoolean(KEY_UNITS, true);
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}

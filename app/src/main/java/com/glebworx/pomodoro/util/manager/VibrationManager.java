package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Objects;

public class VibrationManager {

    private static final int DURATION_SHORT = 500;
    private static final int DURATION_LONG = 2000;

    private Vibrator vibrator;

    public VibrationManager(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void vibrateShort() {
        Objects.requireNonNull(vibrator).vibrate(VibrationEffect.createOneShot(DURATION_SHORT, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public void vibrateLong() {
        Objects.requireNonNull(vibrator).vibrate(VibrationEffect.createOneShot(DURATION_LONG, VibrationEffect.DEFAULT_AMPLITUDE));
    }

}

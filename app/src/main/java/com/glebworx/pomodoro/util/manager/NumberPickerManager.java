package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.widget.NumberPicker;

import com.glebworx.pomodoro.R;

import java.lang.reflect.Method;

public class NumberPickerManager {

    public static void initPicker(Context context, NumberPicker picker, int minValue, int maxValue) {

        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setWrapSelectorWheel(true);
        picker.setFormatter(value -> {
            int minutes = value * DateTimeManager.POMODORO_LENGTH + (value - 1) * DateTimeManager.BREAK_LENGTH;
            if (value == 1) {
                return context.getString(
                        R.string.core_pomodoro,
                        String.valueOf(value),
                        DateTimeManager.formatHHMMString(context, minutes));
            }
            return context.getString(
                    R.string.core_pomodoros,
                    String.valueOf(value),
                    DateTimeManager.formatHHMMString(context, minutes));
        });
        picker.setValue(1);

        try {
            Method method = picker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(picker, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

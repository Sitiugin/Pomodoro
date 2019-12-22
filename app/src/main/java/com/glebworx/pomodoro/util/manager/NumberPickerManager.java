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
            if (value == 1) {
                return context.getString(
                        R.string.core_pomodoro_time,
                        String.valueOf(value),
                        DateTimeManager.formatHHString(1));
            }
            return context.getString(
                    R.string.core_pomodoros_time,
                    String.valueOf(value),
                    DateTimeManager.formatHHString(value));
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

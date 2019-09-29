package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.R;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_AMBER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_BLUE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_CYAN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_DEEP_ORANGE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_DEEP_PURPLE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_GREEN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_INDIGO_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_LIGHT_BLUE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_LIGHT_GREEN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_LIME_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_ORANGE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_PINK_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_PURPLE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_RED_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_TEAL_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_YELLOW_HEX;


public class ColorManager {

    private ColorManager() { }

    public static int getColor(@NonNull Context context, @Nullable String colorTag) {
        if (colorTag == null) {
            return context.getColor(android.R.color.transparent);
        }
        switch (colorTag) {
            case COLOR_RED_HEX:
                return context.getColor(R.color.colorRed);
            case COLOR_PINK_HEX:
                return context.getColor(R.color.colorPink);
            case COLOR_PURPLE_HEX:
                return context.getColor(R.color.colorPurple);
            case COLOR_DEEP_PURPLE_HEX:
                return context.getColor(R.color.colorDeepPurple);
            case COLOR_INDIGO_HEX:
                return context.getColor(R.color.colorIndigo);
            case COLOR_BLUE_HEX:
                return context.getColor(R.color.colorBlue);
            case COLOR_LIGHT_BLUE_HEX:
                return context.getColor(R.color.colorLightBlue);
            case COLOR_CYAN_HEX:
                return context.getColor(R.color.colorCyan);
            case COLOR_TEAL_HEX:
                return context.getColor(R.color.colorTeal);
            case COLOR_GREEN_HEX:
                return context.getColor(R.color.colorGreen);
            case COLOR_LIGHT_GREEN_HEX:
                return context.getColor(R.color.colorLightGreen);
            case COLOR_LIME_HEX:
                return context.getColor(R.color.colorLime);
            case COLOR_YELLOW_HEX:
                return context.getColor(R.color.colorYellow);
            case COLOR_AMBER_HEX:
                return context.getColor(R.color.colorAmber);
            case COLOR_ORANGE_HEX:
                return context.getColor(R.color.colorOrange);
            case COLOR_DEEP_ORANGE_HEX:
                return context.getColor(R.color.colorDeepOrange);
            default:
                return context.getColor(android.R.color.transparent);
        }
    }
}

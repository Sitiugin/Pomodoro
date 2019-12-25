package com.glebworx.pomodoro.util.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.constants.ColorConstants;

import java.util.Random;

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

    public static int getRandomColor() {
        int random = new Random().nextInt(16);
        switch (random) {
            case 0:
                return ColorConstants.rgb(COLOR_RED_HEX);
            case 1:
                return ColorConstants.rgb(COLOR_PINK_HEX);
            case 2:
                return ColorConstants.rgb(COLOR_PURPLE_HEX);
            case 3:
                return ColorConstants.rgb(COLOR_DEEP_PURPLE_HEX);
            case 4:
                return ColorConstants.rgb(COLOR_INDIGO_HEX);
            case 5:
                return ColorConstants.rgb(COLOR_BLUE_HEX);
            case 6:
                return ColorConstants.rgb(COLOR_LIGHT_BLUE_HEX);
            case 7:
                return ColorConstants.rgb(COLOR_CYAN_HEX);
            case 8:
                return ColorConstants.rgb(COLOR_TEAL_HEX);
            case 9:
                return ColorConstants.rgb(COLOR_GREEN_HEX);
            case 10:
                return ColorConstants.rgb(COLOR_LIGHT_GREEN_HEX);
            case 11:
                return ColorConstants.rgb(COLOR_LIME_HEX);
            case 12:
                return ColorConstants.rgb(COLOR_YELLOW_HEX);
            case 13:
                return ColorConstants.rgb(COLOR_AMBER_HEX);
            case 14:
                return ColorConstants.rgb(COLOR_ORANGE_HEX);
            case 15:
                return ColorConstants.rgb(COLOR_DEEP_ORANGE_HEX);
        }
        return 0;
    }
}

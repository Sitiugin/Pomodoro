package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.R;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_ALIZARIN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_AMETHYST_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_CARROT_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_EMERALD_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_PETER_RIVER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_SUNFLOWER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_TURQUOISE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_WET_ASPHALT_HEX;

public class ColorManager {

    private ColorManager() { }

    public static int getColor(@NonNull Context context, @Nullable String colorTag) {
        if (colorTag == null) {
            return context.getColor(android.R.color.transparent);
        }
        switch (colorTag) {
            case COLOR_TURQUOISE_HEX:
                return context.getColor(R.color.colorTurquoise);
            case COLOR_EMERALD_HEX:
                return context.getColor(R.color.colorEmerald);
            case COLOR_PETER_RIVER_HEX:
                return context.getColor(R.color.colorPeterRiver);
            case COLOR_AMETHYST_HEX:
                return context.getColor(R.color.colorAmethyst);
            case COLOR_WET_ASPHALT_HEX:
                return context.getColor(R.color.colorWetAsphalt);
            case COLOR_SUNFLOWER_HEX:
                return context.getColor(R.color.colorSunflower);
            case COLOR_CARROT_HEX:
                return context.getColor(R.color.colorCarrot);
            case COLOR_ALIZARIN_HEX:
                return context.getColor(R.color.colorAlizarin);
            default:
                return context.getColor(android.R.color.transparent);
        }
    }
}

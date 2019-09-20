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

    public static Drawable getDrawable(@NonNull Context context, @Nullable String colorTag) {
        if (colorTag == null) {
            return null;
        }
        switch (colorTag) {
            case COLOR_TURQUOISE_HEX:
                return context.getDrawable(R.drawable.drawable_dot_turquoise);
            case COLOR_EMERALD_HEX:
                return context.getDrawable(R.drawable.drawable_dot_emerald);
            case COLOR_PETER_RIVER_HEX:
                return context.getDrawable(R.drawable.drawable_dot_peter_river);
            case COLOR_AMETHYST_HEX:
                return context.getDrawable(R.drawable.drawable_dot_amethyst);
            case COLOR_WET_ASPHALT_HEX:
                return context.getDrawable(R.drawable.drawable_dot_wet_asphalt);
            case COLOR_SUNFLOWER_HEX:
                return context.getDrawable(R.drawable.drawable_dot_sunflower);
            case COLOR_CARROT_HEX:
                return context.getDrawable(R.drawable.drawable_dot_carrot);
            case COLOR_ALIZARIN_HEX:
                return context.getDrawable(R.drawable.drawable_dot_alizarin);
            default:
                return null;
        }
    }
}

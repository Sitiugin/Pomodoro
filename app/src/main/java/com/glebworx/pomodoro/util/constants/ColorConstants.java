package com.glebworx.pomodoro.util.constants;

import android.graphics.Color;

public class ColorConstants {

    public static final String COLOR_HIGHLIGHT_HEX = "#448AFF";
    public static final String COLOR_GRAY_HEX = "#A9A9A9";

    public static final String COLOR_RED_HEX = "#ff5252";
    public static final String COLOR_PINK_HEX = "#FF4081";
    public static final String COLOR_PURPLE_HEX = "#E040FB";
    public static final String COLOR_DEEP_PURPLE_HEX = "#7C4DFF";
    public static final String COLOR_INDIGO_HEX = "#536DFE";
    public static final String COLOR_BLUE_HEX = "#448AFF";
    public static final String COLOR_LIGHT_BLUE_HEX = "#40C4FF";
    public static final String COLOR_CYAN_HEX = "#18FFFF";
    public static final String COLOR_TEAL_HEX = "#64FFDA";
    public static final String COLOR_GREEN_HEX = "#69F0AE";
    public static final String COLOR_LIGHT_GREEN_HEX = "#B2FF59";
    public static final String COLOR_LIME_HEX = "#EEFF41";
    public static final String COLOR_YELLOW_HEX = "#FFFF00";
    public static final String COLOR_AMBER_HEX = "#FFD740";
    public static final String COLOR_ORANGE_HEX = "#FFAB40";
    public static final String COLOR_DEEP_ORANGE_HEX = "#FF6E40";

    public static final int[] COLORS = {
            ColorConstants.rgb(COLOR_RED_HEX),
            ColorConstants.rgb(COLOR_PINK_HEX),
            ColorConstants.rgb(COLOR_PURPLE_HEX),
            ColorConstants.rgb(COLOR_DEEP_PURPLE_HEX),
            ColorConstants.rgb(COLOR_INDIGO_HEX),
            ColorConstants.rgb(COLOR_BLUE_HEX),
            ColorConstants.rgb(COLOR_LIGHT_BLUE_HEX),
            ColorConstants.rgb(COLOR_CYAN_HEX),
            ColorConstants.rgb(COLOR_TEAL_HEX),
            ColorConstants.rgb(COLOR_GREEN_HEX),
            ColorConstants.rgb(COLOR_LIGHT_GREEN_HEX),
            ColorConstants.rgb(COLOR_YELLOW_HEX),
            ColorConstants.rgb(COLOR_AMBER_HEX),
            ColorConstants.rgb(COLOR_ORANGE_HEX),
            ColorConstants.rgb(COLOR_DEEP_ORANGE_HEX)
    };

    private ColorConstants() { }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

}

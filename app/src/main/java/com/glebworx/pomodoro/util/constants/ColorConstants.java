package com.glebworx.pomodoro.util.constants;

import android.graphics.Color;

public class ColorConstants {

    public static final String COLOR_HIGHLIGHT_HEX = "#84FFFF";

    public static final String COLOR_RED_HEX = "#ff8a80";
    public static final String COLOR_PINK_HEX = "#FF80AB";
    public static final String COLOR_PURPLE_HEX = "#EA80FC";
    public static final String COLOR_DEEP_PURPLE_HEX = "#B388FF";
    public static final String COLOR_INDIGO_HEX = "#8C9EFF";
    public static final String COLOR_BLUE_HEX = "#82B1FF";
    public static final String COLOR_LIGHT_BLUE_HEX = "#80D8FF";
    public static final String COLOR_CYAN_HEX = "#84FFFF";
    public static final String COLOR_TEAL_HEX = "#A7FFEB";
    public static final String COLOR_GREEN_HEX = "#B9F6CA";
    public static final String COLOR_LIGHT_GREEN_HEX = "#CCFF90";
    public static final String COLOR_LIME_HEX = "#F4FF81";
    public static final String COLOR_YELLOW_HEX = "#FFFF8D";
    public static final String COLOR_AMBER_HEX = "#FFE57F";
    public static final String COLOR_ORANGE_HEX = "#FFD180";
    public static final String COLOR_DEEP_ORANGE_HEX = "#FF9E80";

    private ColorConstants() { }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

}

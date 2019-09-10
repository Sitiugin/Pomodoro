package com.glebworx.pomodoro.util.constants;

import android.graphics.Color;

public class ColorConstants {

    public static final String COLOR_HIGHLIGHT_HEX = "#ff5349";
    public static final int COLOR_HIGHLIGHT_INT = rgb(COLOR_HIGHLIGHT_HEX);

    public static final int COLOR_WHITE_INT = Color.parseColor("#FFFFFF");
    public static final int COLOR_BLACK_INT = Color.parseColor("#000000");

    private ColorConstants() { }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

}

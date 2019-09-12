package com.glebworx.pomodoro.util.constants;

import android.graphics.Color;

public class ColorConstants {

    public static final String COLOR_HIGHLIGHT_HEX = "#ff5349";
    public static final int COLOR_HIGHLIGHT_INT = rgb(COLOR_HIGHLIGHT_HEX);

    public static final int COLOR_WHITE_INT = Color.parseColor("#FFFFFF");
    public static final int COLOR_BLACK_INT = Color.parseColor("#000000");

    public static final String COLOR_TURQUOISE_HEX = "#1abc9c"; // yellow-green
    public static final String COLOR_EMERALD_HEX = "#2ecc71"; // green
    public static final String COLOR_PETER_RIVER_HEX = "#3498d";
    public static final String COLOR_AMETHYST_HEX = "#9b59b6";
    public static final String COLOR_WET_ASPHALT_HEX = "#34495e";
    public static final String COLOR_SUNFLOWER_HEX = "#f1c40f"; // yellow
    public static final String COLOR_CARROT_HEX = "#e67e22"; // orange
    public static final String COLOR_ALIZARIN_HEX = "#e74c3c"; // red

    public static final int COLOR_TURQUOISE_INT = rgb(COLOR_TURQUOISE_HEX);
    public static final int COLOR_EMERALD_INT = rgb(COLOR_EMERALD_HEX);
    public static final int COLOR_PETER_RIVER_INT = rgb(COLOR_PETER_RIVER_HEX);
    public static final int COLOR_AMETHYST_INT = rgb(COLOR_AMETHYST_HEX);
    public static final int COLOR_WET_ASPHALT_INT = rgb(COLOR_WET_ASPHALT_HEX);
    public static final int COLOR_SUNFLOWER_INT = rgb(COLOR_SUNFLOWER_HEX);
    public static final int COLOR_CARROT_INT = rgb(COLOR_CARROT_HEX);
    public static final int COLOR_ALIZARIN_INT = rgb(COLOR_ALIZARIN_HEX);

    private ColorConstants() { }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }

}

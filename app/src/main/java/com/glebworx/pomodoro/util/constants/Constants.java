package com.glebworx.pomodoro.util.constants;

import android.graphics.Typeface;

public class Constants {

    private Constants() { }

    public static final String PATTERN_DATE_TIME = "EEEE MMM d, y hh:mm a";
    public static final String PATTERN_DATE = "EEEE MMM d, y";
    public static final String PATTERN_TIME = "HH mm";
    public static final String PATTERN_CALENDAR = "MMM y";

    public static final int MAX_POMODOROS_SESSION = 10;

    public static final int ANIM_DURATION = 400;

    public static final int LENGTH_SNACK_BAR = 5000;

    public static final String SANS_SERIF_LIGHT = "sans-serif-light";

    public static final int RATIO_MS_TO_WEEK = 604800000;

    public static final Typeface TYPEFACE = Typeface.create(SANS_SERIF_LIGHT, Typeface.NORMAL);

}

package com.glebworx.pomodoro.util.manager;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateTimeManager {

    private DateTimeManager() { }

    public static void clearTime(Calendar calendar) {
        calendar.set(Calendar.YEAR, 0);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
    }
}

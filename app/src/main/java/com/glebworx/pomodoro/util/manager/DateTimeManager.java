package com.glebworx.pomodoro.util.manager;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DateTimeManager {

    private DateTimeManager() { }

    public static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }
}

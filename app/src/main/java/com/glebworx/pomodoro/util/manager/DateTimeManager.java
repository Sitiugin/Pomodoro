package com.glebworx.pomodoro.util.manager;

import android.content.Context;

import com.glebworx.pomodoro.R;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeManager {

    private DateTimeManager() { }

    public static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    public static boolean isDateInCurrentWeek(Calendar currentCalendar, Calendar targetCalendar) {
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    public static boolean isDateToday(Calendar currentCalendar, Calendar targetCalendar) {
        return currentCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.DAY_OF_YEAR) == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDateTomorrow(Calendar currentCalendar, Calendar targetCalendar) {
        if (currentCalendar.get(Calendar.YEAR) != targetCalendar.get(Calendar.YEAR)) {
            return false;
        }
        return currentCalendar.get(Calendar.DAY_OF_YEAR) + 1 == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDateYesterday(Calendar currentCalendar, Calendar targetCalendar) {
        if (currentCalendar.get(Calendar.YEAR) != targetCalendar.get(Calendar.YEAR)) {
            return false;
        }
        return currentCalendar.get(Calendar.DAY_OF_YEAR) - 1 == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static String getDateString(Context context,
                                         Calendar currentCalendar,
                                         Calendar targetCalendar,
                                         SimpleDateFormat dateFormat) {

        if (isDateToday(currentCalendar, targetCalendar)) {
            return context.getString(R.string.core_today);
        }
        if (isDateTomorrow(currentCalendar, targetCalendar)) {
            return context.getString(R.string.core_tomorrow);
        }
        if (isDateYesterday(currentCalendar, targetCalendar)) {
            return context.getString(R.string.core_yesterday);
        }
        return dateFormat.format(targetCalendar.getTime());

    }

}

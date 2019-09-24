package com.glebworx.pomodoro.util.manager;

import android.content.Context;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.constants.Constants;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateTimeManager {

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());

    private Context context;
    private Calendar currentCalendar;
    private Calendar targetCalendar;

    public DateTimeManager(Context context, Calendar targetCalendar) {
        this.context = context;
        this.currentCalendar = Calendar.getInstance(Locale.getDefault());
        clearTime(currentCalendar);
        this.targetCalendar = targetCalendar;
        clearTime(targetCalendar);
    }

    public void setCurrentCalendar() {
        this.currentCalendar = Calendar.getInstance(Locale.getDefault());
        clearTime(currentCalendar);
    }

    public void setTargetCalendar(Calendar calendar) {
        this.targetCalendar = calendar;
        clearTime(targetCalendar);
    }

    public static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    public static boolean isDateToday(Calendar currentCalendar, Calendar targetCalendar) {
        return currentCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.DAY_OF_YEAR) == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDateInCurrentWeek(Calendar currentCalendar, Calendar targetCalendar) {
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);
        return week == targetWeek && year == targetYear;
    }

    public static boolean isDateOverdue(Calendar currentCalendar, Calendar targetCalendar) {

        if (currentCalendar.get(Calendar.YEAR) > targetCalendar.get(Calendar.YEAR)) {
            return true;
        }
        if (currentCalendar.get(Calendar.YEAR) < targetCalendar.get(Calendar.YEAR)) {
            return false;
        }
        return currentCalendar.get(Calendar.DAY_OF_YEAR) > targetCalendar.get(Calendar.DAY_OF_YEAR);

    }

    public boolean isDateToday() {
        return currentCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR)
                && currentCalendar.get(Calendar.DAY_OF_YEAR) == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isDateTomorrow() {
        if (currentCalendar.get(Calendar.YEAR) != targetCalendar.get(Calendar.YEAR)) {
            return false;
        }
        return currentCalendar.get(Calendar.DAY_OF_YEAR) + 1 == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isDateYesterday() {
        if (currentCalendar.get(Calendar.YEAR) != targetCalendar.get(Calendar.YEAR)) {
            return false;
        }
        return currentCalendar.get(Calendar.DAY_OF_YEAR) - 1 == targetCalendar.get(Calendar.DAY_OF_YEAR);
    }

    public String getDateString() {

        if (isDateToday()) {
            return context.getString(R.string.core_today);
        }
        if (isDateTomorrow()) {
            return context.getString(R.string.core_tomorrow);
        }
        if (isDateYesterday()) {
            return context.getString(R.string.core_yesterday);
        }
        return dateFormat.format(targetCalendar.getTime());

    }

    public String getDueDateString() {

        if (isDateToday()) {
            return context.getString(R.string.core_due_today);
        }
        if (isDateTomorrow()) {
            return context.getString(R.string.core_due_tomorrow);
        }
        if (isDateYesterday()) {
            return context.getString(R.string.core_due_yesterday);
        }
        return context.getString(R.string.core_due, dateFormat.format(targetCalendar.getTime()));

    }

}

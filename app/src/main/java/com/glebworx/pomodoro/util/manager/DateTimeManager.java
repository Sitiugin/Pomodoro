package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.text.format.DateUtils;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.constants.Constants;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateTimeManager {

    public static final int POMODORO_LENGTH = 25;
    public static final int HOUR_LENGTH = 60;

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

    public static String getDueDateString(Context context, Date date, Date currentDate) {
        return context.getString(R.string.core_due, DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                currentDate.getTime(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL));
    }

    public static String getDateString(Date date, Date currentDate) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                currentDate.getTime(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL));
    }

    public static String formatTimeString(Context context, int minutes) {
        return context.getString(R.string.core_time,
                String.valueOf(minutes / HOUR_LENGTH),
                String.valueOf(minutes % HOUR_LENGTH));
    }

    public static int formatTimeString(int pomodoros) {
        return pomodoros * POMODORO_LENGTH;
    }

}

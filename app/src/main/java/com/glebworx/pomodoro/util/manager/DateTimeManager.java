package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.text.format.DateUtils;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.constants.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateTimeManager {

    public static final int POMODORO_LENGTH = 25;
    public static final int HOUR_LENGTH = 60;

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());
    private static SimpleDateFormat calendarFormat =
            new SimpleDateFormat(Constants.PATTERN_CALENDAR, Locale.getDefault());

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

    public static String getHistoryDateString(Date date, Date currentDate) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                currentDate.getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL));
    }

    public static String getDateString(Date date, Date currentDate) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(
                date.getTime(),
                currentDate.getTime(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL));
    }

    public static String getAxisDateString(long date, long currentDate) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(
                date,
                currentDate,
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL));
    }

    public static String getBarAxisDateString(long date, long currentDate) {
        return String.valueOf(DateUtils.getRelativeTimeSpanString(
                date,
                currentDate,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_MONTH));
    }

    public static String getMMYYString(Date date) {
        return calendarFormat.format(date);
    }

    public static String formatHHMMString(Context context, int minutes) {
        return String.format(Locale.getDefault(), "%d:%02d", minutes / 60, minutes % 60);
    }

    public static int formatHHMMString(int pomodoros) {
        return pomodoros * POMODORO_LENGTH;
    }

    public static String formatMMSSString(Context context, int seconds) {
        return String.format(Locale.getDefault(), "%d:%02d", seconds / 60, seconds % 60);
    }

}

package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import java.util.Date;

public interface IReportHistoryViewPresenter {

    void init();

    void setCalendarDate(Date newDate, boolean updateCalendar);

    void showDatePicker();

}

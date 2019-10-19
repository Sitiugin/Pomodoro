package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.glebworx.pomodoro.ui.fragment.report.view.item.ReportHistoryItem;
import com.mikepenz.fastadapter.FastAdapter;

import java.util.Date;

public interface IReportHistoryViewPresenter {

    void init();

    void setCalendarDate(Date newDate,
                         FastAdapter<ReportHistoryItem> fastAdapter,
                         boolean updateCalendar,
                         boolean scrollToDate);

    void showDatePicker();

    void getHistoryItems();

}

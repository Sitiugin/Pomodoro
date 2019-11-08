package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.glebworx.pomodoro.ui.fragment.report.view.item.ReportHistoryItem;

import java.util.Date;

public interface IReportHistoryView {

    void onInitView();

    void onHistoryReceived(ReportHistoryItem item, String colorTag, long date);

    void onHistoryRequestFailed();

    void onDateChanged(Date newDate, boolean updateCalendar);

    void onShowDatePicker(Date defaultDate);

    void onScrollToPosition(int index);

    void onNoEntryToScrollTo();

}

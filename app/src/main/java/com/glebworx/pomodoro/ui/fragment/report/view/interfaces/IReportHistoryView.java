package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

import io.reactivex.Observable;

public interface IReportHistoryView {

    void onInitView();

    void onHistoryReceived(Observable<DocumentSnapshot> observable);

    void onHistoryRequestFailed();

    void onDateChanged(Date newDate, boolean updateCalendar);

    void onShowDatePicker(Date defaultDate);

    void onScrollToPosition(int index);

    void onNoEntryToScrollTo();

}

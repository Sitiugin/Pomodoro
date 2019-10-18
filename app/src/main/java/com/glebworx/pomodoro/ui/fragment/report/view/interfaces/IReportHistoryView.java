package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.google.firebase.firestore.DocumentChange;

import java.util.Date;

import io.reactivex.Observable;

public interface IReportHistoryView {

    void onInitView();

    void onSubscribed(Observable<DocumentChange> observable);

    void onDateChanged(Date newDate);

    void onShowDatePicker(Date defaultDate);

}

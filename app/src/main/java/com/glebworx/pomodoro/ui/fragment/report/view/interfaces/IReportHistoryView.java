package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.google.firebase.firestore.DocumentChange;

import io.reactivex.Observable;

public interface IReportHistoryView {

    void onInitView();

    void onSubscribed(Observable<DocumentChange> observable);

}

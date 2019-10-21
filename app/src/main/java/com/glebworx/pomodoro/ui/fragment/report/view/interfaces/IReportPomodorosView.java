package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.glebworx.pomodoro.model.ReportPomodoroModel;

import io.reactivex.Observable;

public interface IReportPomodorosView {

    void onInitView();

    void onPomodorosCompletedReceived(Observable<ReportPomodoroModel> observable);

}

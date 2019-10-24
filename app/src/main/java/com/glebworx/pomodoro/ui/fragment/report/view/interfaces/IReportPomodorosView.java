package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.glebworx.pomodoro.model.ReportPomodoroModel;
import com.glebworx.pomodoro.model.ReportPomodoroOverviewModel;

import io.reactivex.Observable;

public interface IReportPomodorosView {

    void onInitView();

    void onObservablesReady(Observable<ReportPomodoroOverviewModel> overviewObservable,
                            Observable<ReportPomodoroModel> observable);

}

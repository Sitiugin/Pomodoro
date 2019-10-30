package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.glebworx.pomodoro.model.report.ReportPomodoroOverviewModel;

import io.reactivex.Observable;

public interface IReportPomodorosView {

    void onInitView();

    void onObservablesReady(Observable<ReportPomodoroOverviewModel> overviewObservable,
                            Observable<LineData> pomodorosCompletedObservable,
                            Observable<BarData> weeklyTrendsObservable);

}

package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.glebworx.pomodoro.model.ReportProjectOverviewModel;

import io.reactivex.Observable;

public interface IReportProjectsView {

    void onInitView();

    void onObservablesReady(Observable<ReportProjectOverviewModel> overviewObservable,
                            Observable<PieData> distributionObservable,
                            Observable<PieData> overdueObservable,
                            Observable<LineData> elapsedTimeObservable);

}

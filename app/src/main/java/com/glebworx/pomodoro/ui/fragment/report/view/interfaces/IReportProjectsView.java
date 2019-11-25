package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;

public interface IReportProjectsView {

    void onInitView();

    void onChartDataEmpty();

    void onInitOverview(String projectsCompletedString,
                        String averageCompletionTimeString,
                        String elapsedTimeString);

    void onInitProjectsDistributionChart(PieData pieData);

    void onInitProjectsOverdueChart(PieData pieData);

    void onInitElapsedTimeChart(LineData lineData);

}

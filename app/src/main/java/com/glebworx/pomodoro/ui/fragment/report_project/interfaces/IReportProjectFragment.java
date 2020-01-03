package com.glebworx.pomodoro.ui.fragment.report_project.interfaces;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;

public interface IReportProjectFragment {

    void onInitView(String projectName,
                    int estimatedTime,
                    int elapsedTime,
                    float progress);

    void onSummaryChanged(int estimatedTime, int elapsedTime, float progress);

    void onInitDistributionChart(PieData pieData);

    void onInitOverdueChart(PieData pieData);

    void onInitElapsedTimeChart(LineData lineData);

    void onDistributionChartDataEmpty();

    void onOverdueChartDataEmpty();

    void onElapsedChartDataEmpty();

}

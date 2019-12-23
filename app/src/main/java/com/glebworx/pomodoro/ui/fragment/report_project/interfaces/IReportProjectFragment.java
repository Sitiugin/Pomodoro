package com.glebworx.pomodoro.ui.fragment.report_project.interfaces;

import com.github.mikephil.charting.data.LineData;

public interface IReportProjectFragment {

    void onInitView(String projectName,
                    int estimatedTime,
                    int elapsedTime,
                    float progress);

    void onSummaryChanged(int estimatedTime, int elapsedTime, float progress);

    void onInitElapsedTimeChart(LineData lineData);

    void onChartDataEmpty();

}

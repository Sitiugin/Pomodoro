package com.glebworx.pomodoro.model;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;

public class ReportPomodoroModel {

    private final int pomodorosCompleted;
    private final LineData distributionData;
    private final BarData trendsData;

    public ReportPomodoroModel() {
        this.pomodorosCompleted = 0;
        this.distributionData = new LineData();
        this.trendsData = new BarData();
    }

    public ReportPomodoroModel(int pomodorosCompleted,
                               LineData distributionData,
                               BarData trendsData) {
        this.pomodorosCompleted = pomodorosCompleted;
        this.distributionData = distributionData;
        this.trendsData = trendsData;
    }

    public int getPomodorosCompleted() {
        return pomodorosCompleted;
    }

    public LineData getDistributionData() {
        return distributionData;
    }

    public BarData getTrendsData() {
        return trendsData;
    }

}

package com.glebworx.pomodoro.model;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;

public class ReportPomodoroModel {

    private int pomodorosCompleted;
    private int maxPomodoros;
    private float avgPerDayPomodoros;
    private float avgPerWeekPomodoros;
    private float avgPerMonthPomodoros;
    private LineData distributionData;
    private BarData trendsData;

    public ReportPomodoroModel() {
        this.pomodorosCompleted = 0;
        this.maxPomodoros = 0;
        this.avgPerDayPomodoros = 0;
        this.avgPerWeekPomodoros = 0;
        this.avgPerMonthPomodoros = 0;
        this.distributionData = new LineData();
        this.trendsData = new BarData();
    }

    public ReportPomodoroModel(int pomodorosCompleted,
                               int maxPomodoros,
                               float avgPerDayPomodoros,
                               float avgPerWeekPomodoros,
                               float avgPerMonthPomodoros,
                               LineData distributionData,
                               BarData trendsData) {
        this.pomodorosCompleted = pomodorosCompleted;
        this.maxPomodoros = maxPomodoros;
        this.avgPerDayPomodoros = avgPerDayPomodoros;
        this.avgPerWeekPomodoros = avgPerWeekPomodoros;
        this.avgPerMonthPomodoros = avgPerMonthPomodoros;
        this.distributionData = distributionData;
        this.trendsData = trendsData;
    }

    public int getPomodorosCompleted() {
        return pomodorosCompleted;
    }

    public void setPomodorosCompleted(int pomodorosCompleted) {
        this.pomodorosCompleted = pomodorosCompleted;
    }

    public int getMaxPomodoros() {
        return maxPomodoros;
    }

    public void setMaxPomodoros(int maxPomodoros) {
        this.maxPomodoros = maxPomodoros;
    }

    public float getAvgPerDayPomodoros() {
        return avgPerDayPomodoros;
    }

    public void setAvgPerDayPomodoros(float avgPerDayPomodoros) {
        this.avgPerDayPomodoros = avgPerDayPomodoros;
    }

    public float getAvgPerWeekPomodoros() {
        return avgPerWeekPomodoros;
    }

    public void setAvgPerWeekPomodoros(float avgPerWeekPomodoros) {
        this.avgPerWeekPomodoros = avgPerWeekPomodoros;
    }

    public float getAvgPerMonthPomodoros() {
        return avgPerMonthPomodoros;
    }

    public void setAvgPerMonthPomodoros(float avgPerMonthPomodoros) {
        this.avgPerMonthPomodoros = avgPerMonthPomodoros;
    }

    public LineData getDistributionData() {
        return distributionData;
    }

    public void setDistributionData(LineData distributionData) {
        this.distributionData = distributionData;
    }

    public BarData getTrendsData() {
        return trendsData;
    }

    public void setTrendsData(BarData trendsData) {
        this.trendsData = trendsData;
    }
}

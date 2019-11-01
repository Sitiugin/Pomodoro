package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;

public interface IReportPomodorosView {

    void onInitView();

    void onInitOverview(String pomodorosCompletedString, String averagePerDayString, String streakString);

    void onInitPomodorosCompletedChart(LineData lineData);

    void onInitWeeklyTrendsChart(BarData barData);

}

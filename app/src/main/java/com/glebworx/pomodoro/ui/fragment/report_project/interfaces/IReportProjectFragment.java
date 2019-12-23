package com.glebworx.pomodoro.ui.fragment.report_project.interfaces;

public interface IReportProjectFragment {

    void onInitView(String projectName,
                    int estimatedTime,
                    int elapsedTime,
                    float progress);

    void onSummaryChanged(int estimatedTime, int elapsedTime, float progress);
}

package com.glebworx.pomodoro.ui.fragment.report.view.interfaces;

public interface IReportProjectsViewPresenter {

    void init(String[] distributionLabels, String[] overueLabels);

    void destroy();
}

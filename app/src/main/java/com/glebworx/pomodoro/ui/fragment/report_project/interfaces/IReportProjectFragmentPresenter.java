package com.glebworx.pomodoro.ui.fragment.report_project.interfaces;

import android.os.Bundle;

public interface IReportProjectFragmentPresenter {

    void init(Bundle arguments, String onTimeLabel, String overdueLabel);

    void destroy();
}

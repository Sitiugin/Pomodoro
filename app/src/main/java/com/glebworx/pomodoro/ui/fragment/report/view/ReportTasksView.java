package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportTasksView;

public class ReportTasksView extends ConstraintLayout implements IReportTasksView {

    public ReportTasksView(Context context) {
        super(context);
    }

    public ReportTasksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReportTasksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}

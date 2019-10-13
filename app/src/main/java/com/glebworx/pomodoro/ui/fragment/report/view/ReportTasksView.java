package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportTasksView;

public class ReportTasksView extends ConstraintLayout implements IReportTasksView {

    private Context context;

    public ReportTasksView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ReportTasksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ReportTasksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.view_report_tasks, this);
        this.context = context;
    }

}

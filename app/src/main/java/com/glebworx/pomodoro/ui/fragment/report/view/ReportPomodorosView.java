package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;

public class ReportPomodorosView extends ConstraintLayout implements IReportPomodorosView {

    public ReportPomodorosView(Context context) {
        super(context);
    }

    public ReportPomodorosView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReportPomodorosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}

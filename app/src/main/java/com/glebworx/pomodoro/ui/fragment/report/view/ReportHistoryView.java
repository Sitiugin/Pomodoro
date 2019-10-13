package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;

public class ReportHistoryView extends ConstraintLayout implements IReportHistoryView {

    public ReportHistoryView(Context context) {
        super(context);
    }

    public ReportHistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReportHistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}

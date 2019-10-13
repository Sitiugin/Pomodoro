package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;

public class ReportPomodorosView extends ConstraintLayout implements IReportPomodorosView {

    private Context context;

    public ReportPomodorosView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ReportPomodorosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ReportPomodorosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.view_report_pomodoros, this);
        this.context = context;
    }

}

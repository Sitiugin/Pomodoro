package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.widget.NestedScrollView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsView;

public class ReportProjectsView extends NestedScrollView implements IReportProjectsView {

    private Context context;
    private ReportProjectsViewPresenter presenter;

    public ReportProjectsView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ReportProjectsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ReportProjectsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.subscribe();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.unsubscribe();
        super.onDetachedFromWindow();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        View rootView = inflate(context, R.layout.view_report_projects, this);
        this.context = context;
        this.presenter = new ReportProjectsViewPresenter();
    }

}

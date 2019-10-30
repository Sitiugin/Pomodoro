package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.report.ReportProjectOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsView;

import io.reactivex.Observable;

public class ReportProjectsView extends NestedScrollView implements IReportProjectsView {

    private View rootView;
    private FrameLayout projectDistributionLayout;
    private PieChart projectDistributionPieChart;
    private FrameLayout projectOverdueLayout;
    private PieChart projectOverduePieChart;
    private FrameLayout elapsedTimeLayout;
    private LineChart elapsedTimeLineChart;
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
    public void onInitView() {
        IChart.initChart(projectDistributionPieChart);
        IChart.initChart(projectOverduePieChart);
        IChart.initChart(elapsedTimeLineChart, false, false, null);
    }

    @Override
    public void onObservablesReady(Observable<ReportProjectOverviewModel> overviewObservable, Observable<PieData> distributionObservable, Observable<PieData> overdueObservable, Observable<LineData> elapsedTimeObservable) {
        // TODO implement
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        rootView = inflate(context, R.layout.view_report_projects, this);
        projectDistributionLayout = rootView.findViewById(R.id.layout_distribution);
        projectDistributionPieChart = rootView.findViewById(R.id.pie_chart_distribution);
        projectOverdueLayout = rootView.findViewById(R.id.layout_overdue);
        projectOverduePieChart = rootView.findViewById(R.id.pie_chart_overdue);
        elapsedTimeLayout = rootView.findViewById(R.id.layout_elapsed_time);
        elapsedTimeLineChart = rootView.findViewById(R.id.line_chart_elapsed_time);
        this.context = context;
        this.presenter = new ReportProjectsViewPresenter(this);
    }

}

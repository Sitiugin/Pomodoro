package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsView;
import com.glebworx.pomodoro.util.manager.DateTimeManager;

import static com.glebworx.pomodoro.util.constants.Constants.ANIM_DURATION;

public class ReportProjectsView extends NestedScrollView implements IReportProjectsView {

    private View rootView;
    private AppCompatTextView projectsCompletedTextView;
    private AppCompatTextView tasksCompletedTextView;
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
    protected void onDetachedFromWindow() {
        presenter.destroy();
        super.onDetachedFromWindow();
    }

    @Override
    public void onInitView() {
        IChart.initChart(projectDistributionPieChart);
        IChart.initChart(projectOverduePieChart);
        IChart.initChart(elapsedTimeLineChart, false, false, null);
        OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.layout_elapsed_time) {
                IChart.expandChart(context, rootView, elapsedTimeLineChart, false);
            }
        };
        elapsedTimeLayout.setOnClickListener(onClickListener);
    }

    @Override
    public void onInitOverview(String projectsCompletedString, String tasksCompletedString) {
        hideOverviewSpinKit();
        projectsCompletedTextView.setText(projectsCompletedString);
        tasksCompletedTextView.setText(tasksCompletedString);
    }

    @Override
    public void onInitProjectsDistributionChart(PieData pieData) {
        hideDistributionSpinKit();
        if (pieData.getEntryCount() > 0) {
            projectDistributionPieChart.setData(pieData);
            projectDistributionPieChart.animateY(ANIM_DURATION);
        }
    }

    @Override
    public void onInitProjectsOverdueChart(PieData pieData) {
        if (pieData.getEntryCount() > 0) {
            projectOverduePieChart.setData(pieData);
            projectOverduePieChart.animateY(ANIM_DURATION);
        }
    }

    @Override
    public void onInitElapsedTimeChart(LineData lineData) {
        hideElapsedTimeSpinKit();
        IChart.initData(lineData, context);
        if (lineData.getEntryCount() > 0) {
            elapsedTimeLineChart.setData(lineData);
            elapsedTimeLineChart.animateY(ANIM_DURATION);
        }
    }

    @Override
    public void onOverviewDataEmpty() {
        hideOverviewSpinKit();
        String emptyTime = DateTimeManager.formatHHMMString(context, 0);
        projectsCompletedTextView.setText(emptyTime);
        tasksCompletedTextView.setText(emptyTime);
    }

    @Override
    public void onDistributionDataEmpty() {
        hideDistributionSpinKit();
        String emptyText = context.getString(R.string.core_text_no_data);
        projectDistributionPieChart.setNoDataText(emptyText);
        projectDistributionPieChart.invalidate();
    }

    @Override
    public void onOverdueDataEmpty() {
        hideOverdueSpinKit();
        String emptyText = context.getString(R.string.core_text_no_data);
        projectOverduePieChart.setNoDataText(emptyText);
        projectOverduePieChart.invalidate();
    }

    @Override
    public void onElapsedTimeDataEmpty() {
        hideElapsedTimeSpinKit();
        elapsedTimeLineChart.setNoDataText(context.getString(R.string.core_text_no_data));
        elapsedTimeLineChart.invalidate();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        rootView = inflate(context, R.layout.view_report_projects, this);
        projectsCompletedTextView = rootView.findViewById(R.id.text_view_projects_completed);
        tasksCompletedTextView = rootView.findViewById(R.id.text_view_tasks_completed);
        projectDistributionLayout = rootView.findViewById(R.id.layout_distribution);
        projectDistributionPieChart = rootView.findViewById(R.id.pie_chart_distribution);
        projectOverdueLayout = rootView.findViewById(R.id.layout_overdue);
        projectOverduePieChart = rootView.findViewById(R.id.pie_chart_overdue);
        elapsedTimeLayout = rootView.findViewById(R.id.layout_elapsed_time);
        elapsedTimeLineChart = rootView.findViewById(R.id.line_chart_elapsed_time);
        this.context = context;
        String[] distributionLabels = context.getResources().getStringArray(R.array.report_projects_array_duration_types);
        String[] overdueLabels = context.getResources().getStringArray(R.array.report_projects_array_overdue);
        this.presenter = new ReportProjectsViewPresenter(this, distributionLabels, overdueLabels);
    }

    private void hideOverviewSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_overview).setVisibility(GONE);
    }

    private void hideDistributionSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_distribution).setVisibility(GONE);
    }

    private void hideOverdueSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_overdue).setVisibility(GONE);
    }

    private void hideElapsedTimeSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_elapsed_time).setVisibility(GONE);
    }

}

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
    private AppCompatTextView averageCompletionTimeTextView;
    private AppCompatTextView elapsedTimeTextView;
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
                IChart.expandChart(context, rootView, elapsedTimeLineChart);
            }
        };
        elapsedTimeLayout.setOnClickListener(onClickListener);
    }

    @Override
    public void onChartDataEmpty() {
        String emptyTime = DateTimeManager.formatHHMMString(context, 0);
        String emptyText = context.getString(R.string.core_text_no_data);
        projectsCompletedTextView.setText(emptyTime);
        averageCompletionTimeTextView.setText(emptyTime);
        elapsedTimeTextView.setText(emptyTime);
        projectDistributionPieChart.setNoDataText(emptyText);
        projectDistributionPieChart.invalidate();
        projectOverduePieChart.setNoDataText(emptyText);
        projectOverduePieChart.invalidate();
        elapsedTimeLineChart.setNoDataText(emptyText);
        elapsedTimeLineChart.invalidate();
    }

    @Override
    public void onInitOverview(String projectsCompletedString, String averageCompletionTimeString, String elapsedTimeString) {
        projectsCompletedTextView.setText(projectsCompletedString);
        averageCompletionTimeTextView.setText(averageCompletionTimeString);
        elapsedTimeTextView.setText(elapsedTimeString);
    }

    @Override
    public void onInitProjectsDistributionChart(PieData pieData) {
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
        elapsedTimeLineChart.setData(lineData);
        elapsedTimeLineChart.animateY(ANIM_DURATION);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        rootView = inflate(context, R.layout.view_report_projects, this);
        projectsCompletedTextView = rootView.findViewById(R.id.text_view_projects_completed);
        averageCompletionTimeTextView = rootView.findViewById(R.id.text_view_average_completion_time);
        elapsedTimeTextView = rootView.findViewById(R.id.text_view_elapsed_time);
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

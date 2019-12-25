package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;

import static com.glebworx.pomodoro.util.constants.Constants.ANIM_DURATION;

public class ReportPomodorosView extends NestedScrollView implements IReportPomodorosView {

    private View rootView;
    private AppCompatTextView pomodorosCompletedTextView;
    private AppCompatTextView averagePerDayTextView;
    private AppCompatTextView streakTextView;
    private FrameLayout pomodorosCompletedLayout;
    private LineChart pomodorosCompletedLineChart;
    private FrameLayout weeklyTrendsLayout;
    private BarChart weeklyTrendsBarChart;

    private Context context;
    private ReportPomodorosViewPresenter presenter;

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

    @Override
    protected void onDetachedFromWindow() {
        presenter.destroy();
        super.onDetachedFromWindow();
    }

    @Override
    public void onInitView() {
        IChart.initChart(pomodorosCompletedLineChart, false, true, null);
        IChart.initChart(weeklyTrendsBarChart, false, false, null);
        OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.layout_pomodoros_completed:
                    IChart.expandChart(context, rootView, pomodorosCompletedLineChart);
                    break;
                case R.id.layout_trends:
                    IChart.expandChart(context, rootView, weeklyTrendsBarChart);
                    break;
            }
        };
        pomodorosCompletedLayout.setOnClickListener(onClickListener);
        weeklyTrendsLayout.setOnClickListener(onClickListener);
    }

    @Override
    public void onInitOverview(String pomodorosCompletedString, String averagePerDayString, String streakString) {
        hideOverviewSpinKit();
        pomodorosCompletedTextView.setText(pomodorosCompletedString);
        averagePerDayTextView.setText(averagePerDayString);
        streakTextView.setText(streakString);
    }

    @Override
    public void onInitPomodorosCompletedChart(LineData lineData) {
        hidePomodorosCompletedSpinKit();
        IChart.initData(lineData, context);
        if (lineData.getEntryCount() > 0) {
            pomodorosCompletedLineChart.setData(lineData);
            pomodorosCompletedLineChart.animateY(ANIM_DURATION);
        }
    }

    @Override
    public void onInitWeeklyTrendsChart(BarData barData) {
        hideWeeklyTrendsSpinKit();
        IChart.initData(barData, context);
        if (barData.getEntryCount() > 0) {
            weeklyTrendsBarChart.setData(barData);
            weeklyTrendsBarChart.animateY(ANIM_DURATION);
        }
    }

    @Override
    public void onOverviewDataEmpty() {
        hideOverviewSpinKit();
        pomodorosCompletedTextView.setText(String.valueOf(0));
        averagePerDayTextView.setText(String.valueOf(0));
        streakTextView.setText(String.valueOf(0));
    }

    @Override
    public void onPomodorosCompletedDataEmpty() {
        hidePomodorosCompletedSpinKit();
        pomodorosCompletedLineChart.setNoDataText(context.getString(R.string.core_text_no_data));
        pomodorosCompletedLineChart.invalidate();
    }

    @Override
    public void onWeeklyTrendsDataEmpty() {
        hideWeeklyTrendsSpinKit();
        weeklyTrendsBarChart.setNoDataText(context.getString(R.string.core_text_no_data));
        weeklyTrendsBarChart.invalidate();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        rootView = inflate(context, R.layout.view_report_pomodoros, this);
        pomodorosCompletedTextView = rootView.findViewById(R.id.text_view_pomodoros_completed);
        averagePerDayTextView = rootView.findViewById(R.id.text_view_average_per_day);
        streakTextView = rootView.findViewById(R.id.text_view_streak);
        pomodorosCompletedLayout = rootView.findViewById(R.id.layout_pomodoros_completed);
        pomodorosCompletedLineChart = rootView.findViewById(R.id.line_chart_pomodoros_completed);
        weeklyTrendsLayout = rootView.findViewById(R.id.layout_trends);
        weeklyTrendsBarChart = rootView.findViewById(R.id.bar_chart_trends);
        this.context = context;
        this.presenter = new ReportPomodorosViewPresenter(this);
    }

    private void hideOverviewSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_overview).setVisibility(GONE);
    }

    private void hidePomodorosCompletedSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_pomodoros_completed).setVisibility(GONE);
    }

    private void hideWeeklyTrendsSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_trends).setVisibility(GONE);
    }

}

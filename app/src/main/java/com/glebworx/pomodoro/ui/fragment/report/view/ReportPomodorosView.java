package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;

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
    public void onInitView() {
        IChart.initChart(pomodorosCompletedLineChart, false, true, null);
        IChart.initChart(weeklyTrendsBarChart, false, false, null);
        OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.layout_pomodoros_completed:
                    expandChart(pomodorosCompletedLineChart);
                    break;
                case R.id.layout_trends:
                    expandChart(weeklyTrendsBarChart);
                    break;
            }
        };
        pomodorosCompletedLayout.setOnClickListener(onClickListener);
        weeklyTrendsLayout.setOnClickListener(onClickListener);
    }

    @Override
    public void onInitOverview(String pomodorosCompletedString, String averagePerDayString, String streakString) {
        pomodorosCompletedTextView.setText(pomodorosCompletedString);
        averagePerDayTextView.setText(averagePerDayString);
        streakTextView.setText(streakString);
    }

    @Override
    public void onInitPomodorosCompletedChart(LineData lineData) {
        pomodorosCompletedLineChart.setData(lineData);
        pomodorosCompletedLineChart.animateY(ANIM_DURATION);
    }

    @Override
    public void onInitWeeklyTrendsChart(BarData barData) {
        weeklyTrendsBarChart.setData(barData);
        weeklyTrendsBarChart.animateY(ANIM_DURATION);
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

    private void expandChart(LineChart chart) {
        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.getPopupWindow(R.layout.popup_line_chart_expanded, true);
        View contentView = popupWindow.getContentView();
        contentView.findViewById(R.id.button_collapse).setOnClickListener(v -> popupWindow.dismiss());
        LineChart expandedChart = contentView.findViewById(R.id.chart_expanded);
        expandedChart.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        expandedChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        IChart.rotateChart(expandedChart);
                        IChart.initChart(expandedChart, true, true, "");
                        expandedChart.setData(chart.getData());
                        expandedChart.animateY(ANIM_DURATION);
                    }
                });
        popupWindow.showAsDropDown(rootView, 0, 0, Gravity.CENTER);
    }

    private void expandChart(BarChart chart) {
        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.getPopupWindow(R.layout.popup_bar_chart_expanded, true);
        View contentView = popupWindow.getContentView();
        contentView.findViewById(R.id.button_collapse).setOnClickListener(v -> popupWindow.dismiss());
        BarChart expandedChart = contentView.findViewById(R.id.chart_expanded);
        expandedChart.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        expandedChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        IChart.rotateChart(expandedChart);
                        IChart.initChart(expandedChart, true, true, "");
                        expandedChart.setData(chart.getData());
                        expandedChart.animateY(ANIM_DURATION);
                    }
                });
        popupWindow.showAsDropDown(rootView, 0, 0, Gravity.CENTER);
    }

}

package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ReportPomodoroOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.glebworx.pomodoro.util.constants.Constants.ANIM_DURATION;

public class ReportPomodorosView extends NestedScrollView implements IReportPomodorosView {

    private AppCompatTextView pomodorosCompletedTextView;
    private AppCompatTextView averagePerDayTextView;
    private AppCompatTextView streakTextView;
    private LineChart pomodorosCompletedLineChart;
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
        IChart.initChart(pomodorosCompletedLineChart, false, null);
        IChart.initChart(weeklyTrendsBarChart, false, null);
        OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.line_chart_pomodoros_completed:
                    expandChart(pomodorosCompletedLineChart);
                    break;
                case R.id.bar_chart_trends:
                    expandChart(weeklyTrendsBarChart);
                    break;
            }
        };
        pomodorosCompletedLineChart.setOnClickListener(onClickListener);
        weeklyTrendsBarChart.setOnClickListener(onClickListener);
    }

    @Override
    public void onObservablesReady(Observable<ReportPomodoroOverviewModel> overviewObservable,
                                   Observable<LineData> pomodorosCompletedObservable,
                                   Observable<BarData> weeklyTrendsObservable) {
        overviewObservable.subscribe(getOverviewObserver());
        pomodorosCompletedObservable.subscribe(getPomodorosCompletedObserver());
        weeklyTrendsObservable.subscribe(getWeeklyTrendsObserver());
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        View rootView = inflate(context, R.layout.view_report_pomodoros, this);
        pomodorosCompletedTextView = rootView.findViewById(R.id.text_view_pomodoros_completed);
        averagePerDayTextView = rootView.findViewById(R.id.text_view_average_per_day);
        streakTextView = rootView.findViewById(R.id.text_view_streak);
        pomodorosCompletedLineChart = rootView.findViewById(R.id.line_chart_pomodoros_completed);
        weeklyTrendsBarChart = rootView.findViewById(R.id.bar_chart_trends);
        this.context = context;
        this.presenter = new ReportPomodorosViewPresenter(this);
    }

    private void expandChart(BarLineChartBase chart) {
        // TODO implement
    }

    private io.reactivex.Observer<ReportPomodoroOverviewModel> getOverviewObserver() {
        return new io.reactivex.Observer<ReportPomodoroOverviewModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReportPomodoroOverviewModel model) {
                pomodorosCompletedTextView.setText(String.valueOf(model.getPomodorosCompleted()));
                averagePerDayTextView.setText(String.valueOf(model.getAveragePerDay()));
                streakTextView.setText(String.valueOf(model.getStreak()));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<LineData> getPomodorosCompletedObserver() {
        return new io.reactivex.Observer<LineData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LineData lineData) {
                pomodorosCompletedLineChart.setData(lineData);
                pomodorosCompletedLineChart.animateY(ANIM_DURATION);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<BarData> getWeeklyTrendsObserver() {
        return new io.reactivex.Observer<BarData>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BarData barData) {
                weeklyTrendsBarChart.setData(barData);
                weeklyTrendsBarChart.animateY(ANIM_DURATION);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

}

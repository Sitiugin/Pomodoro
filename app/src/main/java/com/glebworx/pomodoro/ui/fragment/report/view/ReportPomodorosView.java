package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ReportPomodoroModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ReportPomodorosView extends NestedScrollView implements IReportPomodorosView {

    private AppCompatTextView pomodorosCompletedTextView;
    private LineChart distributionLineChart;
    private BarChart trendsBarChart;

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
        IChart.initChart(distributionLineChart, false, null);
    }

    @Override
    public void onPomodorosCompletedReceived(Observable<ReportPomodoroModel> observable) {
        observable.subscribe(getObserver());
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        View rootView = inflate(context, R.layout.view_report_pomodoros, this);
        pomodorosCompletedTextView = rootView.findViewById(R.id.text_view_pomodoros_completed);
        distributionLineChart = rootView.findViewById(R.id.line_chart_distribution);
        trendsBarChart = rootView.findViewById(R.id.bar_chart_trends);
        this.context = context;
        this.presenter = new ReportPomodorosViewPresenter(this);
    }

    private io.reactivex.Observer<ReportPomodoroModel> getObserver() {
        return new io.reactivex.Observer<ReportPomodoroModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ReportPomodoroModel model) {
                pomodorosCompletedTextView.setText(String.valueOf(model.getPomodorosCompleted()));
                distributionLineChart.setData(model.getDistributionData());
                distributionLineChart.invalidate();
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

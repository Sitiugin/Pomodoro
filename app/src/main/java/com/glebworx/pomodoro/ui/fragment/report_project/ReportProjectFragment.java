package com.glebworx.pomodoro.ui.fragment.report_project;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragment;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentInteractionListener;
import com.glebworx.pomodoro.util.manager.DateTimeManager;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.glebworx.pomodoro.util.constants.Constants.ANIM_DURATION;

public class ReportProjectFragment extends Fragment implements IReportProjectFragment {


    //                                                                                     CONSTANTS

    private static NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale.getDefault());

    static final String ARG_PROJECT_MODEL = "project_model";

    //                                                                                      BINDINGS

    @BindView(R.id.text_view_title)
    AppCompatTextView titleTextView;
    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.text_view_estimated_time)
    AppCompatTextView estimatedTimeTextView;
    @BindView(R.id.text_view_elapsed_time)
    AppCompatTextView elapsedTimeTextView;
    @BindView(R.id.text_view_progress)
    AppCompatTextView progressTextView;
    @BindView(R.id.pie_chart_distribution)
    PieChart distributionPieChart;
    @BindView(R.id.layout_elapsed_time)
    FrameLayout elapsedTimeLayout;
    @BindView(R.id.line_chart_elapsed_time)
    LineChart elapsedTimeLineChart;


    //                                                                                    ATTRIBUTES

    private View rootView;
    private Context context;
    private IReportProjectFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ReportProjectFragmentPresenter presenter;

    //                                                                                  CONSTRUCTORS

    public ReportProjectFragment() {
    }


    //                                                                                       FACTORY

    public static ReportProjectFragment newInstance(ProjectModel projectModel) {
        ReportProjectFragment fragment = new ReportProjectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        fragment.setArguments(args);
        return fragment;
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_report_project, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ReportProjectFragmentPresenter(
                this,
                fragmentListener,
                Objects.requireNonNull(getArguments()));
        return rootView;
    }

    @Override
    public void onDestroyView() {
        presenter.destroy();
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (IReportProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void onInitView(String projectName,
                           int estimatedTime,
                           int elapsedTime,
                           float progress) {
        titleTextView.setText(projectName);
        onSummaryChanged(estimatedTime, elapsedTime, progress);
        IChart.initChart(distributionPieChart);
        IChart.initChart(elapsedTimeLineChart, false, false, null);
        initClickEvents();
    }

    @Override
    public void onSummaryChanged(int estimatedTime, int elapsedTime, float progress) {
        estimatedTimeTextView.setText(DateTimeManager.formatHHMMString(context, estimatedTime));
        elapsedTimeTextView.setText(DateTimeManager.formatHHMMString(context, elapsedTime));
        progressTextView.setText(numberFormat.format(progress > 1 ? 1 : progress));
    }

    @Override
    public void onInitDistributionChart(PieData pieData) {
        hideDistributionSpinKit();
        pieData.getDataSet().setValueTextColor(context.getColor(android.R.color.white));
        distributionPieChart.setData(pieData);
        distributionPieChart.animateY(ANIM_DURATION);
    }

    @Override
    public void onInitElapsedTimeChart(LineData lineData) {
        hideElapsedTimeSpinKit();
        elapsedTimeLineChart.setData(lineData);
        elapsedTimeLineChart.animateY(ANIM_DURATION);
    }

    @Override
    public void onDistributionChartDataEmpty() {
        hideDistributionSpinKit();
        String emptyText = context.getString(R.string.core_text_no_data);
        distributionPieChart.setNoDataText(emptyText);
        distributionPieChart.invalidate();
    }

    @Override
    public void onElapsedChartDataEmpty() {
        hideElapsedTimeSpinKit();
        //String emptyTime = DateTimeManager.formatHHMMString(context, 0);
        String emptyText = context.getString(R.string.core_text_no_data);
        elapsedTimeLineChart.setNoDataText(emptyText);
        elapsedTimeLineChart.invalidate();
    }


    //                                                                                       HELPERS

    private void hideDistributionSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_distribution).setVisibility(View.GONE);
    }

    private void hideElapsedTimeSpinKit() {
        rootView.findViewById(R.id.spin_kit_view_elapsed_time).setVisibility(View.GONE);
    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_close:
                    fragmentListener.onCloseFragment();
                    break;
                case R.id.layout_elapsed_time:
                    IChart.expandChart(context, rootView, elapsedTimeLineChart, false);
                    break;
            }
        };
        closeButton.setOnClickListener(onClickListener);
        elapsedTimeLayout.setOnClickListener(onClickListener);
    }

}

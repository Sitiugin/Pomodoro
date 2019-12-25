package com.glebworx.pomodoro.ui.fragment.report.interfaces;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.constants.ColorConstants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import static com.glebworx.pomodoro.util.constants.Constants.ANIM_DURATION;
import static com.glebworx.pomodoro.util.constants.Constants.RATIO_MS_TO_WEEK;
import static com.glebworx.pomodoro.util.constants.Constants.TYPEFACE;

public interface IChart {

    int SIZE_LABEL = 6;

    static void initDataSet(PieDataSet dataSet) {
        dataSet.setColors(ColorConstants.COLORS);
    }

    static void initDataSet(LineDataSet dataSet, int color) {
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircleHole(false);
        dataSet.setFillColor(color);
        dataSet.setColor(color);
        dataSet.setCircleColors(color);
        //dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
    }

    static void initDataSet(BarDataSet dataSet, int color) {
        dataSet.setColor(color);

    }

    static void initData(LineData data, Context context) {
        initBaseData(data, context);
        data.setValueFormatter(new IChart.AxisEntryYFormatter());
    }

    static void initData(BarData data, Context context) {
        initBaseData(data, context);
        data.setValueFormatter(new IChart.AxisEntryYPomodoroFormatter(context));
    }

    static void initBaseData(BarLineScatterCandleBubbleData data, Context context) {
        data.setDrawValues(true);
        data.setValueTextColor(context.getColor(android.R.color.darker_gray));
        data.setValueTextSize(SIZE_LABEL);
    }

    static void initChart(PieChart chart) {

        Context context = chart.getContext();

        if (context == null) {
            return;
        }

        int colorGray = context.getColor(android.R.color.darker_gray);
        chart.setNoDataTextColor(colorGray);
        chart.setNoDataText("");
        chart.setNoDataTextTypeface(TYPEFACE);

        Description description = new Description();
        description.setText("");

        chart.setDescription(description);
        //description.setTextColor(context.getColor(android.R.color.secondary_text_dark));
        //description.setTypeface(Typeface.create(context.getString(R.string.sans_serif_light), Typeface.NORMAL));*/
        chart.setHoleColor(context.getColor(android.R.color.transparent));

        Legend legend = chart.getLegend();
        legend.setEnabled(true);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.CIRCLE);
        //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(colorGray);

        chart.setDrawEntryLabels(false);
        chart.setUsePercentValues(true);
        //chart.setEntryLabelColor(context.getColor(android.R.color.white));
    }

    static void initChart(LineChart chart, boolean isExpanded, boolean showLegend, String descriptionText) {
        initBaseChart(chart, isExpanded, showLegend, descriptionText);
        chart.setAutoScaleMinMaxEnabled(true);
    }

    static void initChart(BarChart chart, boolean isExpanded, boolean showLegend, String descriptionText) {
        initBaseChart(chart, isExpanded, showLegend, descriptionText);
        //chart.setFitBars(true);

        //chart.setVisibleYRangeMaximum(30, YAxis.AxisDependency.LEFT);
        //chart.setFitBars(true);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new BarAxisEntryXFormatter());
    }

    static void initBaseChart(BarLineChartBase chart, boolean isExpanded, boolean showLegend, String descriptionText) {

        Context context = chart.getContext();

        if (context == null) {
            return;
        }
        int colorGray = context.getColor(android.R.color.darker_gray);
        chart.setNoDataTextColor(colorGray);
        chart.setNoDataText("");
        chart.setNoDataTextTypeface(TYPEFACE);
        chart.setBorderColor(colorGray);
        chart.setBorderWidth(0.5f);
        chart.setDrawBorders(true);
        //chart.setMaxVisibleValueCount(10);
        chart.setTouchEnabled(isExpanded);

        Description description = new Description();
        if (isExpanded) {
            description.setTypeface(TYPEFACE);
            description.setText(descriptionText);
        } else {
            description.setText("");
        }
        chart.setDescription(description);

        Legend legend = chart.getLegend();
        if (showLegend) {
            legend.setDrawInside(isExpanded);
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setWordWrapEnabled(true);
            legend.setTextColor(colorGray);
        }
        legend.setEnabled(showLegend);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(4, true);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(isExpanded);
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new AxisEntryXFormatter());

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(isExpanded);
        yAxisLeft.setDrawLimitLinesBehindData(false);
        yAxisLeft.setDrawLabels(false);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawZeroLine(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(isExpanded);
        yAxisRight.setDrawLimitLinesBehindData(false);
        yAxisRight.setDrawLabels(false);

    }

    static void rotateChart(BarLineChartBase expandedChart) {
        int offset = (expandedChart.getHeight() - expandedChart.getWidth()) / 2;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) expandedChart.getLayoutParams();
        layoutParams.width = expandedChart.getHeight();
        layoutParams.height = expandedChart.getWidth();
        expandedChart.setLayoutParams(layoutParams);
        expandedChart.setTranslationX(-offset);
        expandedChart.setTranslationY(offset);
    }

    static void expandChart(Context context, View rootView, LineChart chart, boolean showLegend) {
        if (chart.getData() == null || chart.getData().getEntryCount() == 0) {
            return;
        }
        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.getPopupWindow(R.layout.popup_line_chart_expanded, true);
        View contentView = popupWindow.getContentView();
        contentView.findViewById(R.id.button_collapse).setOnClickListener(v -> popupWindow.dismiss());
        LineChart expandedChart = contentView.findViewById(R.id.chart_expanded);
        expandedChart.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (chart.getData().getEntryCount() > 0) {
                            expandedChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            IChart.rotateChart(expandedChart);
                            IChart.initChart(expandedChart, true, showLegend, "");
                            expandedChart.setData(chart.getData());
                            expandedChart.animateY(ANIM_DURATION);
                        } else {
                            Toast.makeText(context, R.string.core_text_no_data, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        popupWindow.showAsDropDown(rootView, 0, 0, Gravity.CENTER);
    }

    static void expandChart(Context context, View rootView, BarChart chart) {
        if (chart.getData() == null || chart.getData().getEntryCount() == 0) {
            return;
        }
        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.getPopupWindow(R.layout.popup_bar_chart_expanded, true);
        View contentView = popupWindow.getContentView();
        contentView.findViewById(R.id.button_collapse).setOnClickListener(v -> popupWindow.dismiss());
        BarChart expandedChart = contentView.findViewById(R.id.chart_expanded);
        expandedChart.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (chart.getData().getEntryCount() > 0) {
                            expandedChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            IChart.rotateChart(expandedChart);
                            IChart.initChart(expandedChart, true, false, "");
                            expandedChart.setData(chart.getData());
                            expandedChart.animateY(ANIM_DURATION);
                        } else {
                            Toast.makeText(context, R.string.core_text_no_data, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        popupWindow.showAsDropDown(rootView, 0, 0, Gravity.CENTER);
    }

    class AxisEntryXFormatter extends ValueFormatter {

        private long currentDate;

        AxisEntryXFormatter() {
            this.currentDate = new Date().getTime();
        }

        @Override
        public String getFormattedValue(float value) {
            return DateTimeManager.getAxisDateString((long) value, currentDate);
        }
    }

    class BarAxisEntryXFormatter extends ValueFormatter {

        private long currentDate;

        BarAxisEntryXFormatter() {
            this.currentDate = new Date().getTime();
        }

        @Override
        public String getFormattedValue(float value) {
            return DateTimeManager.getBarAxisDateString((long) value * RATIO_MS_TO_WEEK, currentDate);
        }
    }

    class AxisEntryYFormatter extends ValueFormatter {

        private static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

        @Override
        public String getFormattedValue(float value) {
            return numberFormat.format(value);
        }

    }

    class AxisEntryYPomodoroFormatter extends ValueFormatter {

        private static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        private Context context;

        public AxisEntryYPomodoroFormatter(Context context) {
            super();
            this.context = context;
        }

        @Override
        public String getFormattedValue(float value) {
            int stringId;
            if ((int) value == 1) {
                stringId = R.string.core_pomodoro;
            } else {
                stringId = R.string.core_pomodoros;
            }
            return context.getString(stringId, numberFormat.format(value));
        }

    }

}

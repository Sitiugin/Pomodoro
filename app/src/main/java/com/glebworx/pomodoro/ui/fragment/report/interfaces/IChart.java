package com.glebworx.pomodoro.ui.fragment.report.interfaces;

import android.content.Context;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.manager.DateTimeManager;

import java.util.Date;

import static com.glebworx.pomodoro.util.constants.Constants.TYPEFACE;

public interface IChart {

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

    static void initChart(PieChart chart) {

        Context context = chart.getContext();

        if (context == null) {
            return;
        }

        int colorGray = context.getColor(android.R.color.darker_gray);
        chart.setNoDataTextColor(colorGray);
        chart.setNoDataText(context.getString(R.string.core_text_no_data));
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
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(colorGray);

        chart.setDrawEntryLabels(false);
        chart.setUsePercentValues(false);
    }

    static void initChart(LineChart chart, boolean isExpanded, boolean showLegend, String descriptionText) {
        initBaseChart(chart, isExpanded, showLegend, descriptionText);
    }

    static void initChart(BarChart chart, boolean isExpanded, boolean showLegend, String descriptionText) {
        initBaseChart(chart, isExpanded, showLegend, descriptionText);
        chart.setFitBars(true);
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
        chart.setAutoScaleMinMaxEnabled(true);
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

    class AxisEntryXFormatter extends ValueFormatter {

        private long currentDate;

        public AxisEntryXFormatter() {
            this.currentDate = new Date().getTime();
        }

        @Override
        public String getFormattedValue(float value) {
            return DateTimeManager.getAxisDateString((long) value, currentDate);
        }
    }

    class BarAxisEntryXFormatter extends ValueFormatter {

        private long currentDate;

        public BarAxisEntryXFormatter() {
            this.currentDate = new Date().getTime();
        }

        @Override
        public String getFormattedValue(float value) {
            return DateTimeManager.getBarAxisDateString((long) value * 604800000, currentDate);
        }
    }

}

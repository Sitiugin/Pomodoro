package com.glebworx.pomodoro.util.interfaces;

import android.content.Context;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.glebworx.pomodoro.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.glebworx.pomodoro.util.constants.Constants.PATTERN_DATE_TIME;
import static com.glebworx.pomodoro.util.constants.Constants.TYPEFACE;


public interface IChart {

    static void initDataSet(LineDataSet dataSet, int color) {

        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircleHole(false);
        dataSet.setFillColor(color);
        dataSet.setColor(color);
        dataSet.setCircleColors(color);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

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



    static void initChart(LineChart chart, boolean isExpanded) {

        Context context = chart.getContext();

        if (context == null) {
            return;
        }

        int colorGray = context.getColor(android.R.color.darker_gray);
        chart.setNoDataTextColor(colorGray);
        chart.setNoDataText(context.getString(R.string.core_text_no_data));
        chart.setNoDataTextTypeface(TYPEFACE);
        chart.setBorderColor(colorGray);
        chart.setBorderWidth(1);
        chart.setDrawBorders(true);
        chart.setAutoScaleMinMaxEnabled(true);

        Description description = new Description();
        if (isExpanded) {
            description.setTypeface(TYPEFACE);
            description.setText("Description"); // TODO
        } else {
            description.setText("");
        }
        chart.setDescription(description);

        Legend legend = chart.getLegend();
        if (isExpanded) {
            legend.setDrawInside(true);
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setWordWrapEnabled(true);
            legend.setTextColor(colorGray);
        }
        legend.setEnabled(isExpanded);

        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(4, true);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(isExpanded);
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        SimpleDateFormat dateFormat = new SimpleDateFormat(PATTERN_DATE_TIME, Locale.getDefault());
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return dateFormat.format(new Date((long) value));
            }
        });

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

}

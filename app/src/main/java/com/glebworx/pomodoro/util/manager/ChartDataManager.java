package com.glebworx.pomodoro.util.manager;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.util.constants.ColorConstants;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_GRAY_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_HIGHLIGHT_HEX;
import static com.glebworx.pomodoro.util.constants.Constants.RATIO_MS_TO_WEEK;
import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;

public class ChartDataManager {

    private ChartDataManager() {

    }

    public static LineData getPomodorosCompletedData(List<DocumentSnapshot> documentSnapshots) {

        HistoryModel model;
        String projectName;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        float time;
        Map<String, LineDataSet> dataSetMap = new HashMap<>();
        List<Entry> entries;
        Optional<Entry> optionalEntry;
        Entry entry;
        LineDataSet dataSet;

        for (DocumentSnapshot snapshot : documentSnapshots) {

            // get model
            model = snapshot.toObject(HistoryModel.class);
            if (model == null) {
                continue;
            }

            // add data set object to the map if not already present
            projectName = model.getName();
            dataSet = dataSetMap.get(projectName);
            if (dataSet == null) {
                entries = new ArrayList<>();
                dataSet = new LineDataSet(entries, projectName);
                String colorTag = model.getColorTag();
                if (colorTag == null) {
                    IChart.initDataSet(dataSet, ColorConstants.rgb(COLOR_GRAY_HEX));
                } else {
                    IChart.initDataSet(dataSet, ColorConstants.rgb(model.getColorTag()));
                }
                dataSetMap.put(projectName, dataSet);
            } else {
                entries = dataSet.getValues();
            }

            calendar.setTime(model.getTimestamp());
            DateTimeManager.clearTime(calendar);
            time = calendar.getTimeInMillis();

            optionalEntry = getEntry(time, entries);
            if (optionalEntry.isPresent()) {
                entry = optionalEntry.get();
                entry.setY(entry.getY() + 1);
            } else {
                entry = new Entry(time, 1);
                entries.add(entry);
            }

        }

        LineData lineData = new LineData();

        // put all generated data sets into the object to return
        Set<String> keySet = dataSetMap.keySet();
        for (String key : keySet) {
            dataSet = dataSetMap.get(key);
            if (dataSet == null) {
                continue;
            }
            entries = dataSet.getValues();
            if (entries == null || entries.isEmpty()) {
                continue;
            }
            Collections.sort(entries, new EntryXComparator());

            dataSet.setValues(entries);
            lineData.addDataSet(dataSet);
        }

        return lineData;

    }

    public static BarData getWeeklyTrendsData(List<DocumentSnapshot> documentSnapshots) {

        HistoryModel model;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        float time;
        List<BarEntry> entries = new ArrayList<>();
        Optional<BarEntry> optionalEntry;
        BarEntry entry;

        for (DocumentSnapshot snapshot : documentSnapshots) {

            // get model
            model = snapshot.toObject(HistoryModel.class);
            if (model == null) {
                continue;
            }

            calendar.setTime(model.getTimestamp());
            calendar.set(Calendar.DAY_OF_WEEK, 0);
            DateTimeManager.clearTime(calendar);
            time = Math.round(((float) calendar.getTimeInMillis()) / RATIO_MS_TO_WEEK);

            optionalEntry = getBarEntry(time, entries);
            if (optionalEntry.isPresent()) {
                entry = optionalEntry.get();
                entry.setY(entry.getY() + 1);
            } else {
                entry = new BarEntry(time, 1);
                entries.add(entry);
            }


        }

        BarDataSet dataSet = new BarDataSet(entries, null);
        IChart.initDataSet(dataSet, ColorConstants.rgb(COLOR_HIGHLIGHT_HEX));

        return new BarData(dataSet);

    }

    public static PieData getDistributionData(List<DocumentSnapshot> documentSnapshots) {
        return new PieData(); // TODO implement
    }

    public static LineData getElapsedTimeData(List<DocumentSnapshot> documentSnapshots) {

        HistoryModel model;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        float time;
        List<Entry> entries = new ArrayList<>();
        Optional<Entry> optionalEntry;
        Entry entry;

        for (DocumentSnapshot snapshot : documentSnapshots) {

            // get model
            model = snapshot.toObject(HistoryModel.class);
            if (model == null) {
                continue;
            }

            int elapsedTime = model.getElapsedTime();
            if (elapsedTime == 0) {
                elapsedTime = POMODORO_LENGTH;
            }

            calendar.setTime(model.getTimestamp());
            DateTimeManager.clearTime(calendar);
            time = calendar.getTimeInMillis();

            optionalEntry = getEntry(time, entries);
            if (optionalEntry.isPresent()) {
                entry = optionalEntry.get();
                entry.setY(entry.getY() + elapsedTime);
            } else {
                entry = new Entry(time, elapsedTime);
                entries.add(entry);
            }

        }

        Collections.sort(entries, new EntryXComparator());

        for (int i = 1; i < entries.size(); i++) {
            entry = entries.get(i);
            entry.setY(entry.getY() + entries.get(i - 1).getY());

        }
        for (int i = 0; i < entries.size(); i++) {
            entry = entries.get(i);
            entry.setY(entry.getY() / 60);
        }

        LineDataSet dataSet = new LineDataSet(entries, null);
        IChart.initDataSet(dataSet, ColorConstants.rgb(ColorConstants.COLOR_HIGHLIGHT_HEX));

        return new LineData(dataSet);

    }

    private static Optional<Entry> getEntry(float time, List<Entry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

    private static Optional<BarEntry> getBarEntry(float time, List<BarEntry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

}

package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.ReportPomodoroOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosViewPresenter;
import com.glebworx.pomodoro.util.constants.ColorConstants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ReportPomodorosViewPresenter implements IReportPomodorosViewPresenter {

    private @NonNull
    IReportPomodorosView presenterListener;

    ReportPomodorosViewPresenter(@NonNull IReportPomodorosView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        presenterListener.onInitView();
        HistoryApi.getPomodoroCompletionHistory(this::handleHistory);
    }

    private void handleHistory(Task<QuerySnapshot> task) {

        QuerySnapshot result = task.getResult();

        if (task.isSuccessful() && result != null) {

            Observable<ReportPomodoroOverviewModel> overviewObservable = getOverviewObservable(result);
            overviewObservable = overviewObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Observable<LineData> pomodorosCompletedObservable = getPomodorosCompletedObservable(result);
            pomodorosCompletedObservable = pomodorosCompletedObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Observable<BarData> weeklyTrendsObservable = getWeeklyTrendsObservable(result);
            weeklyTrendsObservable = weeklyTrendsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            presenterListener.onObservablesReady(overviewObservable, pomodorosCompletedObservable, weeklyTrendsObservable);

        }

    }

    private Observable<ReportPomodoroOverviewModel> getOverviewObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            ReportPomodoroOverviewModel model = new ReportPomodoroOverviewModel();
            initOverview(model, snapshot.getDocuments());
            emitter.onNext(model);
            emitter.onComplete();

        });
    }

    private Observable<LineData> getPomodorosCompletedObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            emitter.onNext(getPomodorosCompletedData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private Observable<BarData> getWeeklyTrendsObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            emitter.onNext(getWeeklyTrendsData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private void initOverview(ReportPomodoroOverviewModel model,
                              List<DocumentSnapshot> documentSnapshots) {
        model.setPomodorosCompleted(documentSnapshots.size());
    }

    private LineData getPomodorosCompletedData(List<DocumentSnapshot> documentSnapshots) {

        HistoryModel model;
        String projectName;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        long time;
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
                IChart.initDataSet(dataSet, ColorConstants.rgb(model.getColorTag()));
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

    private BarData getWeeklyTrendsData(List<DocumentSnapshot> documentSnapshots) {

        HistoryModel model;
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        long time;
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
            DateTimeManager.clearTime(calendar);
            //calendar.set(Calendar.DAY_OF_WEEK, 0);
            time = calendar.getTimeInMillis();

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
        IChart.initDataSet(dataSet, ColorConstants.rgb(ColorConstants.COLOR_RED_HEX));

        return new BarData(dataSet);

    }

    private Optional<Entry> getEntry(long time, List<Entry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

    private Optional<BarEntry> getBarEntry(long time, List<BarEntry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

}

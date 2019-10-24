package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.ReportPomodoroModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosViewPresenter;
import com.glebworx.pomodoro.util.constants.ColorConstants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneOffset;
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
        if (task.isSuccessful() && task.getResult() != null) {
            Observable<ReportPomodoroModel> observable = getObservable(task.getResult());
            observable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            presenterListener.onPomodorosCompletedReceived(observable);
        }
    }

    private Observable<ReportPomodoroModel> getObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {

            List<DocumentSnapshot> documentSnapshots = snapshot.getDocuments();
            ReportPomodoroModel model = new ReportPomodoroModel();
            initData(model, documentSnapshots);

            emitter.onNext(model);
            emitter.onComplete();

        });
    }

    private void initData(ReportPomodoroModel reportPomodoroModel, List<DocumentSnapshot> documentSnapshots) {

        reportPomodoroModel.setPomodorosCompleted(documentSnapshots.size());

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        LocalDate localDate;
        Map<String, LineDataSet> dataSetMap = new HashMap<>();

        List<Entry> entries;
        Optional<Entry> optionalEntry;
        Entry entry;
        long time;
        LineDataSet dataSet;
        HistoryModel model;
        String projectName;

        int maxPerDay = 0;

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

            //entries.add(new Entry(time, 1));
            optionalEntry = getEntry(time, entries);
            if (optionalEntry.isPresent()) {
                entry = optionalEntry.get();
                //entry = new Entry(time, entry.getY() + 1);
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

        reportPomodoroModel.setDistributionData(lineData);

    }

    private Optional<Entry> getEntry(long time, List<Entry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

    private Map<LocalDate, Entry> getDayEntryMap(List<DocumentSnapshot> documentSnapshots) {

        Map<LocalDate, Entry> intermediateEntryMap = new HashMap<>();
        //Calendar calendar = Calendar.getInstance(Locale.getDefault());
        LocalDate localDate;
        HistoryModel model;
        String projectName;

        for (DocumentSnapshot snapshot : documentSnapshots) {

            model = snapshot.toObject(HistoryModel.class);

            if (model == null) {
                continue;
            }

            projectName = model.getName();
            localDate = LocalDate.ofEpochDay(model.getTimestamp().getTime());

            Entry entry = intermediateEntryMap.get(localDate);
            if (entry == null) {
                entry = new Entry(localDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC), 1);
                intermediateEntryMap.put(localDate, entry);
            } else {
                entry.setY(entry.getY() + 1);
            }

        }

        return intermediateEntryMap;

    }

    private BarData getTrendsData(List<DocumentSnapshot> documentSnapshots) {
        HistoryModel model;
        for (DocumentSnapshot snapshot : documentSnapshots) {
            model = snapshot.toObject(HistoryModel.class);
            if (model == null) {
                continue;
            }
            // TODO implement
        }
        return new BarData();
    }

}

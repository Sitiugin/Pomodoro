package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

            ReportPomodoroModel model = new ReportPomodoroModel(
                    documentSnapshots.size(),
                    getDistributionData(documentSnapshots),
                    getTrendsData(documentSnapshots));

            emitter.onNext(model);
            emitter.onComplete();

        });
    }

    private LineData getDistributionData(List<DocumentSnapshot> documentSnapshots) {

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
            //localDate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            //time = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
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
            lineData.addDataSet(dataSetMap.get(key));
        }

        return lineData;

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
        }
        return new BarData();
    }

}

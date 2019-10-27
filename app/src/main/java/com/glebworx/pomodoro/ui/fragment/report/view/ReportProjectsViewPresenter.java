package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.ReportPomodoroOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsViewPresenter;
import com.glebworx.pomodoro.util.constants.ColorConstants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.reactivex.Observable;

public class ReportProjectsViewPresenter implements IReportProjectsViewPresenter {

    private @NonNull
    IReportProjectsView presenterListener;

    ReportProjectsViewPresenter(@NonNull IReportProjectsView presenterListener) {
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

            /*Observable<ReportPomodoroOverviewModel> overviewObservable = getOverviewObservable(result);
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

            presenterListener.onObservablesReady(overviewObservable, pomodorosCompletedObservable, weeklyTrendsObservable);*/

        }

    }

    private Observable<ReportPomodoroOverviewModel> getOverviewObservable(QuerySnapshot snapshot) {
        /*return Observable.create(emitter -> {
            ReportPomodoroOverviewModel model = new ReportPomodoroOverviewModel();
            initOverview(model, snapshot.getDocuments());
            emitter.onNext(model);
            emitter.onComplete();

        });*/
        return null;
    }

    private Observable<PieData> getProjectsDistributionObservable(QuerySnapshot snapshot) {
        /*return Observable.create(emitter -> {
            emitter.onNext(getPomodorosCompletedData(snapshot.getDocuments()));
            emitter.onComplete();

        });*/
        return null;
    }

    private Observable<PieData> getProjectsOverdueObservable(QuerySnapshot snapshot) {
        /*return Observable.create(emitter -> {
            emitter.onNext(getPomodorosCompletedData(snapshot.getDocuments()));
            emitter.onComplete();

        });*/
        return null;
    }

    private Observable<LineData> getElapsedTimeObservable(QuerySnapshot snapshot) {
        /*return Observable.create(emitter -> {
            emitter.onNext(getWeeklyTrendsData(snapshot.getDocuments()));
            emitter.onComplete();

        });*/
        return null;
    }

    private void initOverview(ReportPomodoroOverviewModel overviewModel,
                              List<DocumentSnapshot> documentSnapshots) {

        overviewModel.setPomodorosCompleted(documentSnapshots.size());

        HistoryModel model;
        List<Date> dates = new ArrayList<>();
        Date date;
        Date minDate = new Date();
        int totalPomodoros = 0;

        for (DocumentSnapshot snapshot : documentSnapshots) {
            model = snapshot.toObject(HistoryModel.class); // get model
            if (model != null && model.getEventType().equals(HistoryModel.EVENT_POMODORO_COMPLETED)) {
                date = model.getTimestamp();
                dates.add(date);
                if (date.compareTo(minDate) < 0) {
                    minDate = date;
                }
                totalPomodoros++;
            }
        }

        long daysElapsed = ChronoUnit.DAYS.between(minDate.toInstant(), new Date().toInstant());

        overviewModel.setAveragePerDay((float) totalPomodoros / daysElapsed);

        Collections.sort(dates);

        LocalDate current;
        LocalDate previous = dates
                .get(dates.size() - 1)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        if (!previous.equals(LocalDate.now()) && !previous.equals(LocalDate.now().minusDays(1))) {
            overviewModel.setStreak(0);
            return;
        }

        int streak = 1;
        for (int i = dates.size() - 1; i > 0; i--) {
            current = previous;
            previous = dates.get(i - 1)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            if (previous.equals(current)) {
                continue;
            }
            if (previous.plusDays(1).equals(current)) {
                streak++;
            } else {
                overviewModel.setStreak(streak);
                return;
            }
        }

        overviewModel.setStreak(streak);

    }

    private LineData getPomodorosCompletedData(List<DocumentSnapshot> documentSnapshots) {

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

    private Optional<Entry> getEntry(float time, List<Entry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

}

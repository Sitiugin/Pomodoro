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
import com.glebworx.pomodoro.model.report.ReportPomodoroOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosViewPresenter;
import com.glebworx.pomodoro.util.constants.ColorConstants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_HIGHLIGHT_HEX;

public class ReportPomodorosViewPresenter implements IReportPomodorosViewPresenter {

    private @NonNull
    IReportPomodorosView presenterListener;
    private CompositeDisposable compositeDisposable;

    ReportPomodorosViewPresenter(@NonNull IReportPomodorosView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        presenterListener.onInitView();
        compositeDisposable = new CompositeDisposable();
        HistoryApi.getPomodoroCompletionHistory(this::handleHistory);
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    private void handleHistory(Task<QuerySnapshot> task) {

        QuerySnapshot result = task.getResult();

        if (task.isSuccessful() && result != null) {

            Observable<ReportPomodoroOverviewModel> overviewObservable = getOverviewObservable(result);
            overviewObservable = overviewObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            Observable<LineData> pomodorosCompletedObservable = getPomodorosCompletedObservable(result);
            pomodorosCompletedObservable = pomodorosCompletedObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            Observable<BarData> weeklyTrendsObservable = getWeeklyTrendsObservable(result);
            weeklyTrendsObservable = weeklyTrendsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            overviewObservable.subscribe(getOverviewObserver());
            pomodorosCompletedObservable.subscribe(getPomodorosCompletedObserver());
            weeklyTrendsObservable.subscribe(getWeeklyTrendsObserver());

            if (result.isEmpty()) {
                presenterListener.onChartDataEmpty();
            }

        } else {
            presenterListener.onChartDataEmpty();
        }

    }

    private Observable<ReportPomodoroOverviewModel> getOverviewObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            ReportPomodoroOverviewModel model = new ReportPomodoroOverviewModel();
            initOverview(model, snapshot.getDocuments());
            emitter.onNext(model);
            emitter.onComplete();
        });
    }

    private Observable<LineData> getPomodorosCompletedObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(getPomodorosCompletedData(snapshot.getDocuments()));
            emitter.onComplete();
        });
    }

    private Observable<BarData> getWeeklyTrendsObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(getWeeklyTrendsData(snapshot.getDocuments()));
            emitter.onComplete();
        });
    }

    private io.reactivex.Observer<ReportPomodoroOverviewModel> getOverviewObserver() {
        return new io.reactivex.Observer<ReportPomodoroOverviewModel>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(ReportPomodoroOverviewModel model) {
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
                presenterListener.onInitOverview(
                        numberFormat.format(model.getPomodorosCompleted()),
                        numberFormat.format(model.getAveragePerDay()),
                        numberFormat.format(model.getStreak()));
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<LineData> getPomodorosCompletedObserver() {
        return new io.reactivex.Observer<LineData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(LineData lineData) {
                presenterListener.onInitPomodorosCompletedChart(lineData);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<BarData> getWeeklyTrendsObserver() {
        return new io.reactivex.Observer<BarData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(BarData barData) {
                presenterListener.onInitWeeklyTrendsChart(barData);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
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

        overviewModel.setAveragePerDay(daysElapsed == 0 ? 0 : (float) totalPomodoros / daysElapsed);

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
                String colorTag = model.getColorTag();
                if (colorTag == null) {
                    IChart.initDataSet(dataSet, ColorConstants.rgb(COLOR_HIGHLIGHT_HEX));
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

        lineData.setValueFormatter(new IChart.AxisEntryYFormatter());

        return lineData;

    }

    private BarData getWeeklyTrendsData(List<DocumentSnapshot> documentSnapshots) {

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
            DateTimeManager.clearTime(calendar);
            calendar.set(Calendar.DAY_OF_WEEK, 0);
            time = (float) calendar.getTimeInMillis() / 604800000;

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

        BarData barData = new BarData(dataSet);
        barData.setValueFormatter(new IChart.AxisEntryYFormatter());

        return barData;

    }

    private Optional<Entry> getEntry(float time, List<Entry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

    private Optional<BarEntry> getBarEntry(float time, List<BarEntry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

}

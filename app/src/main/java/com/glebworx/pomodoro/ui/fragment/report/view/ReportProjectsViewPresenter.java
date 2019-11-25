package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.report.ReportProjectOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IChart;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsViewPresenter;
import com.glebworx.pomodoro.util.constants.ColorConstants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;

public class ReportProjectsViewPresenter implements IReportProjectsViewPresenter {

    private @NonNull
    IReportProjectsView presenterListener;
    private CompositeDisposable compositeDisposable;

    ReportProjectsViewPresenter(@NonNull IReportProjectsView presenterListener) {
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

            Observable<ReportProjectOverviewModel> overviewObservable = getOverviewObservable(result);
            overviewObservable = overviewObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Observable<PieData> projectsDistributionObservable = getProjectsDistributionObservable(result);
            projectsDistributionObservable = projectsDistributionObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Observable<PieData> overdueObservable = getProjectsOverdueObservable(result);
            overdueObservable = overdueObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            Observable<LineData> elapsedTimeObservable = getElapsedTimeObservable(result);
            elapsedTimeObservable = elapsedTimeObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            overviewObservable.subscribe(getOverviewObserver());
            projectsDistributionObservable.subscribe(getProjectsDistributionObserver());
            overdueObservable.subscribe(getProjectsOverdueObserver());
            elapsedTimeObservable.subscribe(getElapsedTimeObserver());

            if (result.isEmpty()) {
                presenterListener.onChartDataEmpty();
            }

        } else {
            presenterListener.onChartDataEmpty();
        }

    }

    private Observable<ReportProjectOverviewModel> getOverviewObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            ReportProjectOverviewModel model = new ReportProjectOverviewModel();
            initOverview(model, snapshot.getDocuments());
            emitter.onNext(model);
            emitter.onComplete();

        });
    }

    private Observable<PieData> getProjectsDistributionObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(getProjectsDistributionData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private Observable<PieData> getProjectsOverdueObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(getProjectsOverdueData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private Observable<LineData> getElapsedTimeObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(getElapsedTimeData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private io.reactivex.Observer<ReportProjectOverviewModel> getOverviewObserver() {
        return new io.reactivex.Observer<ReportProjectOverviewModel>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(ReportProjectOverviewModel model) {
                /*presenterListener.onInitOverview(
                        String.valueOf(model.getPomodorosCompleted()),
                        String.format(
                                Locale.getDefault(),
                                FORMAT_DECIMAL_1PT,
                                model.getAveragePerDay()),
                        String.valueOf(model.getStreak()));*/
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<PieData> getProjectsDistributionObserver() {
        return new io.reactivex.Observer<PieData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(PieData pieData) {
                presenterListener.onInitProjectsDistributionChart(pieData);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<PieData> getProjectsOverdueObserver() {
        return new io.reactivex.Observer<PieData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(PieData pieData) {
                presenterListener.onInitProjectsOverdueChart(pieData);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<LineData> getElapsedTimeObserver() {
        return new io.reactivex.Observer<LineData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(LineData lineData) {
                presenterListener.onInitElapsedTimeChart(lineData);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void initOverview(ReportProjectOverviewModel overviewModel,
                              List<DocumentSnapshot> documentSnapshots) {



        /*overviewModel.setPomodorosCompleted(documentSnapshots.size());

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

        overviewModel.setStreak(streak);*/

    }

    private PieData getProjectsDistributionData(List<DocumentSnapshot> documentSnapshots) {
        return new PieData(); // TODO implement
    }

    private PieData getProjectsOverdueData(List<DocumentSnapshot> documentSnapshots) {
        return new PieData(); // TODO implement
    }

    private LineData getElapsedTimeData(List<DocumentSnapshot> documentSnapshots) {

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

            calendar.setTime(model.getTimestamp());
            DateTimeManager.clearTime(calendar);
            time = calendar.getTimeInMillis();

            optionalEntry = getEntry(time, entries);
            if (optionalEntry.isPresent()) {
                entry = optionalEntry.get();
                entry.setY(entry.getY() + POMODORO_LENGTH);
            } else {
                entry = new Entry(time, POMODORO_LENGTH);
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

    private Optional<Entry> getEntry(float time, List<Entry> entries) {
        return entries.stream()
                .filter(entry -> time == entry.getX())
                .findAny();
    }

}

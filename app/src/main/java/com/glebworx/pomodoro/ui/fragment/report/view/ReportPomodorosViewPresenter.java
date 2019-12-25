package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.report.ReportPomodoroOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosViewPresenter;
import com.glebworx.pomodoro.util.manager.ChartDataManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

        } else {
            presenterListener.onOverviewDataEmpty();
            presenterListener.onPomodorosCompletedDataEmpty();
            presenterListener.onWeeklyTrendsDataEmpty();
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
            emitter.onNext(ChartDataManager.getPomodorosCompletedData(snapshot.getDocuments()));
            emitter.onComplete();
        });
    }

    private Observable<BarData> getWeeklyTrendsObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(ChartDataManager.getWeeklyTrendsData(snapshot.getDocuments()));
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
                if (lineData.getEntryCount() > 0) {
                    presenterListener.onInitPomodorosCompletedChart(lineData);
                } else {
                    presenterListener.onPomodorosCompletedDataEmpty();
                }
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
                if (barData.getEntryCount() > 0) {
                    presenterListener.onInitWeeklyTrendsChart(barData);
                } else {
                    presenterListener.onWeeklyTrendsDataEmpty();
                }
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

}

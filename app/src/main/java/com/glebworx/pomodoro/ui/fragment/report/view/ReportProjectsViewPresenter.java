package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.report.ReportProjectOverviewModel;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportProjectsViewPresenter;
import com.glebworx.pomodoro.util.manager.ReportDataManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        HistoryApi.getPomodoroCompletionHistory(this::handlePomodoroCompletionHistory); // TODO monitor elapsed time, model should have project name
        HistoryApi.getProjectTaskCompletionHistory(this::handleProjectTaskCompletionHistory);
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    private void handlePomodoroCompletionHistory(Task<QuerySnapshot> task) {

        QuerySnapshot result = task.getResult();

        if (task.isSuccessful() && result != null) {

            Observable<PieData> projectsDistributionObservable = getProjectsDistributionObservable(result);
            projectsDistributionObservable = projectsDistributionObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            Observable<PieData> overdueObservable = getProjectsOverdueObservable(result);
            overdueObservable = overdueObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            Observable<LineData> elapsedTimeObservable = getElapsedTimeObservable(result);
            elapsedTimeObservable = elapsedTimeObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            projectsDistributionObservable.subscribe(getProjectsDistributionObserver());
            overdueObservable.subscribe(getProjectsOverdueObserver());
            elapsedTimeObservable.subscribe(getElapsedTimeObserver());

        } else {
            presenterListener.onDistributionDataEmpty();
            presenterListener.onElapsedTimeDataEmpty();
        }

    }

    private void handleProjectTaskCompletionHistory(Task<QuerySnapshot> task) {

        QuerySnapshot result = task.getResult();

        if (task.isSuccessful() && result != null) {

            Observable<ReportProjectOverviewModel> overviewObservable = getOverviewObservable(result);
            overviewObservable = overviewObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            overviewObservable.subscribe(getOverviewObserver());

        } else {
            presenterListener.onOverviewDataEmpty();
        }

    }

    private Observable<ReportProjectOverviewModel> getOverviewObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            ReportProjectOverviewModel model = ReportDataManager.getProjectOverviewModel(snapshot.getDocuments());
            emitter.onNext(model);
            emitter.onComplete();

        });
    }

    private Observable<PieData> getProjectsDistributionObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(ReportDataManager.getProjectDistributionData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private Observable<PieData> getProjectsOverdueObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(ReportDataManager.getProjectsOverdueData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private Observable<LineData> getElapsedTimeObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(ReportDataManager.getElapsedTimeData(snapshot.getDocuments()));
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
                NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
                presenterListener.onInitOverview(
                        numberFormat.format(model.getProjectsCompleted()),
                        numberFormat.format(model.getTasksCompleted()));
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
                if (pieData.getEntryCount() > 0) {
                    presenterListener.onInitProjectsDistributionChart(pieData);
                } else {
                    presenterListener.onDistributionDataEmpty();
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

    private io.reactivex.Observer<PieData> getProjectsOverdueObserver() {
        return new io.reactivex.Observer<PieData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(PieData pieData) {
                if (pieData.getEntryCount() > 0) {
                    presenterListener.onInitProjectsOverdueChart(pieData);
                } else {
                    presenterListener.onOverdueDataEmpty();
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

    private io.reactivex.Observer<LineData> getElapsedTimeObserver() {
        return new io.reactivex.Observer<LineData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(LineData lineData) {
                if (lineData.getEntryCount() > 0) {
                    presenterListener.onInitElapsedTimeChart(lineData);
                } else {
                    presenterListener.onElapsedTimeDataEmpty();
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

}

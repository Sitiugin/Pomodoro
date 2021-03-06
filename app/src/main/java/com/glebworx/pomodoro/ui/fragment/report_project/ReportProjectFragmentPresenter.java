package com.glebworx.pomodoro.ui.fragment.report_project;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragment;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentPresenter;
import com.glebworx.pomodoro.util.manager.ReportDataManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.ui.fragment.report_project.ReportProjectFragment.ARG_PROJECT_MODEL;

public class ReportProjectFragmentPresenter implements IReportProjectFragmentPresenter {

    private @NonNull
    IReportProjectFragment presenterListener;
    private @NonNull
    IReportProjectFragmentInteractionListener interactionListener;
    private @NonNull
    ProjectModel projectModel;

    private CompositeDisposable compositeDisposable;

    ReportProjectFragmentPresenter(@NonNull IReportProjectFragment presenterListener,
                                   @NonNull IReportProjectFragmentInteractionListener interactionListener,
                                   @NonNull Bundle arguments,
                                   @NonNull String onTimeLabel,
                                   @NonNull String overdueLabel) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(arguments, onTimeLabel, overdueLabel);
    }

    @Override
    public void init(Bundle arguments, String onTimeLabel, String overdueLabel) {

        projectModel = Objects.requireNonNull(arguments.getParcelable(ARG_PROJECT_MODEL));
        compositeDisposable = new CompositeDisposable();

        String projectName = projectModel.getName();

        Observable<DocumentSnapshot> projectObservable = getProjectObservable(projectName);
        projectObservable = projectObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        projectObservable.subscribe(getProjectObserver());

        TaskApi.getTasks(projectName, task -> handleTasks(task, onTimeLabel, overdueLabel));

        HistoryApi.getProjectCompletionHistory(projectName, this::handleHistory);

        presenterListener.onInitView(
                projectName,
                projectModel.getEstimatedTime(),
                projectModel.getElapsedTime(),
                projectModel.getProgress());

    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    private void handleTasks(Task<QuerySnapshot> task, String onTimeLabel, String overdueLabel) {

        QuerySnapshot result = task.getResult();

        if (task.isSuccessful() && result != null) {

            Observable<PieData> distributionObservable = getDistributionObservable(result);
            distributionObservable = distributionObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            distributionObservable.subscribe(getDistributionObserver());

            Observable<PieData> overdueObservable = getOverdueObservable(result, onTimeLabel, overdueLabel);
            overdueObservable = overdueObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            overdueObservable.subscribe(getOverdueObserver());

        } else {
            presenterListener.onDistributionChartDataEmpty();
        }

    }

    private void handleHistory(Task<QuerySnapshot> task) {

        QuerySnapshot result = task.getResult();

        if (task.isSuccessful() && result != null) {

            Observable<LineData> elapsedTimeObservable = getElapsedTimeObservable(result);
            elapsedTimeObservable = elapsedTimeObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            elapsedTimeObservable.subscribe(getElapsedTimeObserver());

        } else {
            presenterListener.onElapsedChartDataEmpty();
        }

    }

    private Observable<DocumentSnapshot> getProjectObservable(@NonNull String projectName) {
        return Observable.create(emitter -> {
            ListenerRegistration taskEventListenerRegistration = ProjectApi.addProjectEventListener(
                    projectName,
                    (documentSnapshot, e) -> {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        if (e != null) {
                            emitter.onError(e);
                            return;
                        }
                        if (documentSnapshot == null) {
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    });
            emitter.setCancellable(taskEventListenerRegistration::remove);
        });
    }

    private io.reactivex.Observer<DocumentSnapshot> getProjectObserver() {
        return new io.reactivex.Observer<DocumentSnapshot>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentSnapshot documentSnapshot) {

                ProjectModel model = documentSnapshot.toObject(ProjectModel.class);
                if (model == null) {
                    return;
                }

                boolean isSummaryDifferent = projectModel.getEstimatedTime() != model.getEstimatedTime()
                        || projectModel.getElapsedTime() != model.getElapsedTime()
                        || projectModel.getProgress() != model.getProgress();

                projectModel.updateFromModel(model);

                if (isSummaryDifferent) {
                    presenterListener.onSummaryChanged(
                            projectModel.getEstimatedTime(),
                            projectModel.getElapsedTime(),
                            projectModel.getProgress());
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

    private Observable<PieData> getDistributionObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(ReportDataManager.getDistributionData(snapshot.getDocuments()));
            emitter.onComplete();

        });
    }

    private io.reactivex.Observer<PieData> getDistributionObserver() {
        return new io.reactivex.Observer<PieData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(PieData pieData) {
                if (pieData.getEntryCount() > 0) {
                    presenterListener.onInitDistributionChart(pieData);
                } else {
                    presenterListener.onDistributionChartDataEmpty();
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

    private Observable<PieData> getOverdueObservable(QuerySnapshot snapshot,
                                                     String onTimeLabel,
                                                     String overdueLabel) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(ReportDataManager.getOverdueData(
                    snapshot.getDocuments(),
                    onTimeLabel,
                    overdueLabel));
            emitter.onComplete();

        });
    }

    private io.reactivex.Observer<PieData> getOverdueObserver() {
        return new io.reactivex.Observer<PieData>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(PieData pieData) {
                if (pieData.getEntryCount() > 0) {
                    presenterListener.onInitOverdueChart(pieData);
                } else {
                    presenterListener.onOverdueChartDataEmpty();
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

    private Observable<LineData> getElapsedTimeObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(ReportDataManager.getElapsedTimeData(snapshot.getDocuments()));
            emitter.onComplete();

        });
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
                    presenterListener.onElapsedChartDataEmpty();
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

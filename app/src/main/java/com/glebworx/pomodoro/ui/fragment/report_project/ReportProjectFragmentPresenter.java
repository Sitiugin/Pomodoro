package com.glebworx.pomodoro.ui.fragment.report_project;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragment;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentPresenter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

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
                                   @NonNull Bundle arguments) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(arguments);
    }

    @Override
    public void init(Bundle arguments) {
        projectModel = Objects.requireNonNull(arguments.getParcelable(ARG_PROJECT_MODEL));
        compositeDisposable = new CompositeDisposable();

        String projectName = projectModel.getName();

        Observable<DocumentSnapshot> projectObservable = getProjectObservable(projectName);
        projectObservable = projectObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        projectObservable.subscribe(getProjectObserver());

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

}

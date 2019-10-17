package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryViewPresenter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ReportHistoryViewPresenter implements IReportHistoryViewPresenter {

    private @NonNull
    IReportHistoryView presenterListener;
    private @NonNull
    Observable<DocumentChange> projectsObservable;

    public ReportHistoryViewPresenter(@NonNull IReportHistoryView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }


    private Observable<DocumentChange> getProjectEventObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = ProjectApi.addModelEventListener((querySnapshot, e) -> {
                if (e != null) {
                    emitter.onError(e);
                    return;
                }
                if (querySnapshot == null || querySnapshot.isEmpty()) {
                    return;
                }
                List<DocumentChange> documentChanges = querySnapshot.getDocumentChanges();
                for (DocumentChange change : documentChanges) {
                    emitter.onNext(change);
                }
            });
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    @Override
    public void init() {
        projectsObservable = getProjectEventObservable();
        presenterListener.onInitView();
    }

    @Override
    public void subscribe() {
        Observable<DocumentChange> observable = projectsObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        presenterListener.onSubscribed(observable);
    }

    @Override
    public void unsubscribe() {
        projectsObservable.unsubscribeOn(Schedulers.io());
    }

}

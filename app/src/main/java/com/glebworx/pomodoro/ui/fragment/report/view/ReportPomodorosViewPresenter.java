package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.ReportPomodoroModel;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosViewPresenter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

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
            ReportPomodoroModel model = new ReportPomodoroModel(snapshot.getDocuments().size());
            emitter.onNext(model);
            emitter.onComplete();
        });
    }

}

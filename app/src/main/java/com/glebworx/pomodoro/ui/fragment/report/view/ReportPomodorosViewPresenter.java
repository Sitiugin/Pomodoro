package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.ReportPomodoroModel;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportPomodorosViewPresenter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

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
        return new LineData();
    }

    private BarData getTrendsData(List<DocumentSnapshot> documentSnapshots) {
        return new BarData();
    }

}

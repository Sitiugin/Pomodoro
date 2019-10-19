package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryViewPresenter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ReportHistoryViewPresenter implements IReportHistoryViewPresenter {

    private @NonNull
    IReportHistoryView presenterListener;
    private @NonNull
    Date calendarDate;

    public ReportHistoryViewPresenter(@NonNull IReportHistoryView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        calendarDate = new Date();
        presenterListener.onInitView();
        HistoryApi.getHistory(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Observable<DocumentSnapshot> observable = getObservable(task.getResult())
                        .observeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                presenterListener.onHistoryReceived(observable);
            } else {
                presenterListener.onHistoryRequestFailed();
            }
        });
    }

    @Override
    public void setCalendarDate(Date newDate, boolean updateCalendar) {
        calendarDate = newDate;
        presenterListener.onDateChanged(calendarDate, updateCalendar);
    }

    @Override
    public void showDatePicker() {
        presenterListener.onShowDatePicker(calendarDate);
    }

    private Observable<DocumentSnapshot> getObservable(QuerySnapshot snapshot) {
        return Observable.fromIterable(snapshot.getDocuments());
    }

}

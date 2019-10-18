package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryViewPresenter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ReportHistoryViewPresenter implements IReportHistoryViewPresenter {

    private @NonNull
    IReportHistoryView presenterListener;
    private @NonNull
    Observable<DocumentChange> observable;
    private Date calendarDate;

    public ReportHistoryViewPresenter(@NonNull IReportHistoryView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        observable = getObservable();
        calendarDate = new Date();
        presenterListener.onInitView();
    }

    @Override
    public void subscribe() {
        Observable<DocumentChange> observable = this.observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        presenterListener.onSubscribed(observable);
    }

    @Override
    public void unsubscribe() {
        observable.unsubscribeOn(Schedulers.io());
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

    private Observable<DocumentChange> getObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = HistoryApi.addAllHistoryEventListener((querySnapshot, e) -> {
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

}

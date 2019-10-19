package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryViewPresenter;
import com.glebworx.pomodoro.ui.fragment.report.view.item.ReportHistoryItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;

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
    public void setCalendarDate(Date newDate,
                                FastAdapter<ReportHistoryItem> fastAdapter,
                                boolean updateCalendar,
                                boolean scrollToDate) {
        calendarDate = newDate;
        presenterListener.onDateChanged(calendarDate, updateCalendar);
        if (scrollToDate) {
            int index = getIndexByDate(fastAdapter, newDate);
            if (index > -1) {
                presenterListener.onScrollToPosition(index);
            } else {
                presenterListener.onNoEntryToScrollTo();
            }
        }
    }

    @Override
    public void showDatePicker() {
        presenterListener.onShowDatePicker(calendarDate);
    }

    private Observable<DocumentSnapshot> getObservable(QuerySnapshot snapshot) {
        return Observable.fromIterable(snapshot.getDocuments());
    }

    private int getIndexByDate(FastAdapter<ReportHistoryItem> adapter, Date date) {
        int count = adapter.getItemCount();
        long minDiff = -1;
        int index = -1;
        long currentTime = date.getTime();
        for (int i = 0; i < count; i++) {
            long diff = Math.abs(currentTime - adapter.getItem(i).getTimestamp().getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                index = i;
            }
        }
        return index;
    }

}

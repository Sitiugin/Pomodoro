package com.glebworx.pomodoro.ui.fragment.report.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryViewPresenter;
import com.glebworx.pomodoro.ui.fragment.report.view.item.ReportHistoryItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ReportHistoryViewPresenter implements IReportHistoryViewPresenter {

    private @NonNull
    IReportHistoryView presenterListener;
    private @NonNull
    Date calendarDate;

    private DocumentSnapshot startAfterSnapshot;
    private CompositeDisposable compositeDisposable;

    ReportHistoryViewPresenter(@NonNull IReportHistoryView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        calendarDate = new Date();
        presenterListener.onInitView();
        startAfterSnapshot = null;
        compositeDisposable = new CompositeDisposable();
        HistoryApi.getHistory(this::handleHistory);
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
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

    @Override
    public void getHistoryItems() {
        if (startAfterSnapshot != null) {
            HistoryApi.getHistoryAfter(startAfterSnapshot, this::handleHistory);
        }
    }

    private void handleHistory(Task<QuerySnapshot> task) {
        if (task.isSuccessful() && task.getResult() != null) {
            Observable<DocumentSnapshot> observable = getObservable(task.getResult());
            if (observable != null) {
                observable = observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io());
                observable.subscribe(getObserver());
            }
        } else {
            presenterListener.onHistoryRequestFailed();
        }
    }

    private Observable<DocumentSnapshot> getObservable(QuerySnapshot snapshot) {
        int snapshotSize = snapshot.getDocuments().size();
        if (snapshotSize == 0) {
            startAfterSnapshot = null;
            return null;
        }
        startAfterSnapshot = snapshot.getDocuments().get(snapshotSize - 1);
        return Observable.fromIterable(snapshot.getDocuments());
    }

    private io.reactivex.Observer<DocumentSnapshot> getObserver() {
        return new io.reactivex.Observer<DocumentSnapshot>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentSnapshot documentSnapshot) {
                HistoryModel model = documentSnapshot.toObject(HistoryModel.class);
                if (model == null) {
                    return;
                }
                ReportHistoryItem item = new ReportHistoryItem(model);
                presenterListener.onHistoryReceived(item, model.getColorTag(), model.getTimestamp().getTime());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
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

package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.google.firebase.firestore.DocumentChange;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ReportHistoryView extends ConstraintLayout implements IReportHistoryView {

    private Context context;
    private ReportHistoryViewPresenter presenter;

    public ReportHistoryView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ReportHistoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ReportHistoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.subscribe();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.unsubscribe();
        super.onDetachedFromWindow();
    }

    @Override
    public void onInitView() {

    }

    @Override
    public void onSubscribed(Observable<DocumentChange> observable) {
        observable.subscribe(getObserver());
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.view_report_history, this);
        this.context = context;
        this.presenter = new ReportHistoryViewPresenter(this);
    }

    private io.reactivex.Observer<DocumentChange> getObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocumentChange documentChange) {

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

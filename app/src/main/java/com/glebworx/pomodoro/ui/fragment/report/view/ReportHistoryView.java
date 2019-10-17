package com.glebworx.pomodoro.ui.fragment.report.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.google.firebase.firestore.DocumentChange;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ReportHistoryView extends ConstraintLayout implements IReportHistoryView {

    @BindView(R.id.button_date)
    AppCompatButton dateButton;
    @BindView(R.id.calendar_view)
    CompactCalendarView calendarView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Context context;
    private ReportHistoryViewPresenter presenter;
    private Unbinder unbinder;

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
    protected void onFinishInflate() {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this);
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
        unbinder.unbind();
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
                switch (documentChange.getType()) {
                    case ADDED:
                        HistoryModel model = documentChange.getDocument().toObject(HistoryModel.class);
                        calendarView.addEvent(new Event(context.getColor(R.color.colorHighlight), model.getTimestamp().getTime()));
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        break;
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

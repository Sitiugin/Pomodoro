package com.glebworx.pomodoro.ui.fragment.report.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.glebworx.pomodoro.ui.fragment.report.view.item.ReportHistoryItem;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.google.firebase.firestore.DocumentChange;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.IntStream;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

public class ReportHistoryView extends ConstraintLayout implements IReportHistoryView {

    private AppCompatButton dateButton;
    private CompactCalendarView calendarView;
    private RecyclerView recyclerView;

    private Context context;
    private Activity activity;
    private ItemAdapter<ReportHistoryItem> historyAdapter;
    private FastAdapter<ReportHistoryItem> fastAdapter;
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
        historyAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        initDateButton();
        initCalendarView();
        initRecyclerView();
    }

    @Override
    public void onSubscribed(Observable<DocumentChange> observable) {
        observable.subscribe(getObserver());
    }

    @Override
    public void onDateChanged(Date newDate, boolean updateCalendar) {
        dateButton.setText(DateTimeManager.getMMYYString(newDate));
        if (updateCalendar) {
            calendarView.setCurrentDate(newDate);
        }
    }

    @Override
    public void onShowDatePicker(Date defaultDate) {
        if (activity == null) {
            return;
        }
        AlertDialog alertDialog = DialogManager.showDialog(activity, R.id.container_main, R.layout.dialog_date_picker);
        DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(defaultDate);
        Objects.requireNonNull(datePicker).init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                getDateChangeListener(alertDialog));
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        View rootView = inflate(context, R.layout.view_report_history, this);
        dateButton = rootView.findViewById(R.id.button_date);
        calendarView = rootView.findViewById(R.id.calendar_view);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
        this.presenter = new ReportHistoryViewPresenter(this);
    }

    private void initDateButton() {
        dateButton.setText(DateTimeManager.getMMYYString(new Date()));
        dateButton.setOnClickListener(view -> presenter.showDatePicker());
    }

    private DatePicker.OnDateChangedListener getDateChangeListener(AlertDialog alertDialog) {
        return (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            presenter.setCalendarDate(calendar.getTime(), true);
            alertDialog.dismiss();
        };
    }

    private void initCalendarView() {
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                presenter.setCalendarDate(firstDayOfNewMonth, false);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new AlphaCrossFadeAnimator());
        fastAdapter.addAdapter(0, historyAdapter);
        fastAdapter.setHasStableIds(true);
        recyclerView.setAdapter(fastAdapter);
    }

    private io.reactivex.Observer<DocumentChange> getObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocumentChange documentChange) {
                HistoryModel model = documentChange.getDocument().toObject(HistoryModel.class);
                ReportHistoryItem item = new ReportHistoryItem(model);
                if (documentChange.getType() == ADDED) {
                    historyAdapter.add(item);
                    calendarView.addEvent(new Event(ColorManager.getColor(context, model.getColorTag()), model.getTimestamp().getTime()));
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

    private int getHistoryItemIndex(@NonNull String id) {
        return IntStream.range(0, historyAdapter.getAdapterItems().size())
                .filter(i -> id.equals(historyAdapter.getAdapterItems().get(i).getId()))
                .findFirst().orElse(-1);
    }

}

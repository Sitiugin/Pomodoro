package com.glebworx.pomodoro.ui.fragment.report.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.view.interfaces.IReportHistoryView;
import com.glebworx.pomodoro.ui.fragment.report.view.item.ReportHistoryItem;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter_extensions.scroll.EndlessRecyclerOnScrollListener;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ReportHistoryView extends ConstraintLayout implements IReportHistoryView {

    private AppCompatButton dateButton;
    private CompactCalendarView calendarView;
    private RecyclerView recyclerView;

    private Context context;
    private Activity activity;
    private LinearLayoutManager layoutManager;
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
    protected void onDetachedFromWindow() {
        presenter.destroy();
        super.onDetachedFromWindow();
    }

    @Override
    public void onInitView() {
        historyAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        layoutManager = new LinearLayoutManager(context);
        initDateButton();
        initCalendarView();
        initRecyclerView();
    }

    @Override
    public void onHistoryReceived(ReportHistoryItem item, String colorTag, long date) {
        historyAdapter.add(item);
        calendarView.addEvent(new Event(ColorManager.getColor(context, colorTag), date));
    }

    @Override
    public void onHistoryRequestFailed() {
        Toast.makeText(context, R.string.report_history_toast_connection_failed, Toast.LENGTH_LONG).show();
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

    @Override
    public void onScrollToPosition(int index) {
        layoutManager.scrollToPositionWithOffset(index, 0);
    }

    @Override
    public void onNoEntryToScrollTo() {
        Toast.makeText(context, R.string.report_history_toast_no_entries_to_scroll_to, Toast.LENGTH_LONG).show();
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
            presenter.setCalendarDate(calendar.getTime(), fastAdapter, true, true);
            alertDialog.dismiss();
        };
    }

    private void initCalendarView() {
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                presenter.setCalendarDate(dateClicked, fastAdapter, false, true);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                presenter.setCalendarDate(firstDayOfNewMonth, fastAdapter, false, false);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty_history));
        recyclerView.setItemAnimator(new AlphaCrossFadeAnimator());
        fastAdapter.addAdapter(0, historyAdapter);
        fastAdapter.setHasStableIds(true);
        recyclerView.setAdapter(fastAdapter);
        attachScrollListener();
        attachEndlessScrollListener();
    }

    private void attachScrollListener() {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);

                if (fastAdapter.getItemCount() == 0) {
                    return;
                }

                int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstVisiblePosition < 0) {
                    return;
                }

                ReportHistoryItem historyItem = fastAdapter.getItem(firstVisiblePosition);
                if (historyItem == null) {
                    return;
                }

                Date date = historyItem.getTimestamp();
                calendarView.setCurrentDate(date);
                dateButton.setText(DateTimeManager.getMMYYString(date));

            }

        });

    }

    private void attachEndlessScrollListener() {
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore(int currentPage) {
                presenter.getHistoryItems();
            }
        });
    }

}

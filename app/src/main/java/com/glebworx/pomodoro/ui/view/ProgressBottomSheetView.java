package com.glebworx.pomodoro.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.activity.MainActivity;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewInteractionListener;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewPresenter;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.SharedPrefsManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.triggertrap.seekarc.SeekArc;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;


public class ProgressBottomSheetView
        extends ConstraintLayout
        implements IProgressBottomSheetView, View.OnClickListener {


    //                                                                                       BINDING

    @BindView(R.id.progress_bar) MaterialProgressBar progressBar;
    @BindView(R.id.text_view_task) AppCompatTextView taskTextView;
    @BindView(R.id.text_view_pomodoro_number) AppCompatTextView pomodoroNumberTextView;
    @BindView(R.id.text_view_status) AppCompatTextView statusTextView;
    @BindView(R.id.text_view_time_remaining) AppCompatTextView timeRemainingTextView;
    @BindView(R.id.button_start_stop) AppCompatImageButton startStopButton;
    @BindView(R.id.button_daily_target)
    AppCompatButton dailyTargetButton;
    @BindView(R.id.text_view_daily_target)
    AppCompatTextView dailyTargetTextView;
    @BindView(R.id.text_view_time_remaining_large) AppCompatTextView timeRemainingLargeTextView;
    @BindView(R.id.seek_arc) SeekArc seekArc;
    @BindView(R.id.fab_start_stop_large) FloatingActionButton startStopFab;
    @BindView(R.id.button_cancel) AppCompatImageButton cancelButton;
    @BindView(R.id.button_complete) AppCompatImageButton completeButton;
    @BindView(R.id.spin_kit_view_large)
    SpinKitView spinKitView;


    //                                                                                     CONSTANTS

    private static final int DURATION_PERCENT = POMODORO_LENGTH * 600;


    //                                                                                    ATTRIBUTES

    private Context context;
    private ConstraintSet constraintSet;
    private IProgressBottomSheetViewInteractionListener bottomSheetListener;
    private int bottomSheetState;
    private Unbinder unbinder;
    private ProgressProgressBottomSheetViewPresenter presenter;
    private final Object object = new Object();


    //                                                                                  CONSTRUCTORS

    public ProgressBottomSheetView(Context context) {
        super(context);
        init(context,null, 0);
    }

    public ProgressBottomSheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ProgressBottomSheetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    //                                                                                     LIFECYCLE

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            bottomSheetListener = (IProgressBottomSheetViewInteractionListener) context;
            startStopButton.setOnClickListener(this);
            startStopFab.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            completeButton.setOnClickListener(this);
            dailyTargetButton.setOnClickListener(this);
        } else {
            bottomSheetListener = null;
            startStopButton.setOnClickListener(null);
            startStopFab.setOnClickListener(null);
            cancelButton.setOnClickListener(null);
            completeButton.setOnClickListener(null);
            dailyTargetButton.setOnClickListener(null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unbinder.unbind();
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_stop:
            case R.id.fab_start_stop_large:
                presenter.handleStartStopClick();
                break;
            case R.id.button_cancel:
                presenter.cancelSession((MainActivity) context);
                break;
            case R.id.button_complete:
                presenter.completeTask();
                break;
            case R.id.button_daily_target:
                presenter.showDailyTargetDialog(context);
        }
    }

    @Override
    public void onTaskSet(String name) {
        synchronized (object) {
            taskTextView.setText(name);
            statusTextView.setText(R.string.bottom_sheet_text_status_idle);
            String timeRemainingString = DateTimeManager.formatMMSSString(context, POMODORO_LENGTH * 60);
            timeRemainingLargeTextView.setText(timeRemainingString);
            timeRemainingTextView.setText(timeRemainingString);
        }
    }

    @Override
    public void onTaskStarted() {
        synchronized (object) {
            startStopButton.setImageResource(R.drawable.ic_pause_highlight);
            startStopFab.setImageResource(R.drawable.ic_pause_black);
            statusTextView.setText(R.string.bottom_sheet_text_status_active);
            spinKitView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onTaskResumed() {
        synchronized (object) {
            startStopButton.setImageResource(R.drawable.ic_pause_highlight);
            startStopFab.setImageResource(R.drawable.ic_pause_black);
            statusTextView.setText(R.string.bottom_sheet_text_status_active);
            spinKitView.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onTaskPaused() {
        synchronized (object) {
            startStopButton.setImageResource(R.drawable.ic_play_highlight);
            startStopFab.setImageResource(R.drawable.ic_play_black);
            statusTextView.setText(R.string.bottom_sheet_text_status_paused);
            if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
                timeRemainingTextView.setText(timeRemainingLargeTextView.getText());
                progressBar.setProgress(seekArc.getProgress());
            } else if (bottomSheetState == BottomSheetBehavior.STATE_COLLAPSED) {
                timeRemainingLargeTextView.setText(timeRemainingTextView.getText());
                seekArc.setProgress(progressBar.getProgress());
            }
            spinKitView.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onPomodoroCompleted(boolean isSuccessful) {
        Toast.makeText(
                context,
                isSuccessful
                        ? R.string.bottom_sheet_toast_pomodoro_completed_success
                        : R.string.bottom_sheet_toast_pomodoro_completed_failed,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTodayCountUpdated(int newCount) {
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        int target = sharedPrefsManager.getPomodoroTarget();
        if (target == 0) {
            dailyTargetButton.setText(R.string.core_empty);
            dailyTargetButton.setTextColor(context.getColor(android.R.color.darker_gray));
        } else {
            dailyTargetButton.setText(context.getString(
                    R.string.core_ratio,
                    String.valueOf(newCount),
                    String.valueOf(target)));
            if (newCount >= target) {
                dailyTargetButton.setTextColor(context.getColor(R.color.colorHighlight));
            } else {
                dailyTargetButton.setTextColor(context.getColor(android.R.color.black));
            }
        }
    }

    @Override
    public void onPomodoroTargetUpdated(int completedToday, int newTarget) {

    }

    @Override
    public void onTaskDataChanged(int pomodorosAllocated, int pomodorosCompleted) {
        pomodoroNumberTextView.setText(context.getString(
                R.string.core_ratio,
                String.valueOf(pomodorosCompleted),
                String.valueOf(pomodorosAllocated)));
    }

    @Override
    public void onTick(long millisUntilFinished) {

        String minutesUntilFinished = DateTimeManager.formatMMSSString(context, (int) (millisUntilFinished / 1000));
        int progress = 100 - (int) (millisUntilFinished / DURATION_PERCENT);

        if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
            synchronized (object) {
                timeRemainingLargeTextView.setText(minutesUntilFinished);
                if (seekArc.getProgress() != progress) {
                    seekArc.setProgress(progress);
                }
            }
        } else if (bottomSheetState == BottomSheetBehavior.STATE_COLLAPSED) {
            synchronized (object) {
                timeRemainingTextView.setText(minutesUntilFinished);
                if (progressBar.getProgress() != progress) {
                    progressBar.setProgress(progress);
                }
            }
        }

    }

    @Override
    public void onClearViews() {
        synchronized (object) {
            startStopButton.setImageResource(R.drawable.ic_play_highlight);
            startStopFab.setImageResource(R.drawable.ic_play_black);
            statusTextView.setText(R.string.bottom_sheet_text_status_idle);
            timeRemainingLargeTextView.setText(null);
            timeRemainingTextView.setText(null);
            seekArc.setProgress(0);
            progressBar.setProgress(0);
        }
    }

    @Override
    public void onHideBottomSheet() {
        bottomSheetListener.onHideBottomSheet();
    }

    @Override
    public void expandBottomSheetViews() {

        this.bottomSheetState = BottomSheetBehavior.STATE_EXPANDED;

        TransitionManager.endTransitions(this);
        TransitionManager.beginDelayedTransition(this);
        constraintSet.clone(this);

        // animate progress
        constraintSet.setVisibility(R.id.seek_arc, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.progress_bar, ConstraintSet.INVISIBLE);

        // animate remaining time
        constraintSet.setVisibility(R.id.text_view_time_remaining, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.text_view_time_remaining_large, ConstraintSet.VISIBLE);

        // animate status
        constraintSet.connect(
                R.id.text_view_status,
                ConstraintSet.TOP,
                R.id.text_view_time_remaining_large,
                ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.text_view_status,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START);
        constraintSet.connect(
                R.id.text_view_status,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END);

        // animate task
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.TOP,
                R.id.text_view_status,
                ConstraintSet.BOTTOM);
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END);

        // animate pomodoro number
        constraintSet.connect(R.id.text_view_pomodoro_number,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START);
        constraintSet.connect(R.id.text_view_pomodoro_number,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END);

        // animate distractions
        constraintSet.setVisibility(R.id.text_view_daily_target, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_daily_target, ConstraintSet.VISIBLE);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.VISIBLE);

        constraintSet.applyTo(this);

    }

    @Override
    public void collapseBottomSheetViews() {

        this.bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;

        TransitionManager.endTransitions(this);
        TransitionManager.beginDelayedTransition(this);
        constraintSet.clone(this);

        // animate progress
        constraintSet.setVisibility(R.id.seek_arc, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.progress_bar, ConstraintSet.VISIBLE);

        // animate remaining time
        constraintSet.setVisibility(R.id.text_view_time_remaining, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.text_view_time_remaining_large, ConstraintSet.INVISIBLE);

        // animate status
        constraintSet.connect(
                R.id.text_view_status,
                ConstraintSet.TOP,
                R.id.text_view_task,
                ConstraintSet.BOTTOM);
        constraintSet.connect(R.id.text_view_status,
                ConstraintSet.START,
                R.id.text_view_task,
                ConstraintSet.START);
        constraintSet.clear(R.id.text_view_status, ConstraintSet.END);

        // animate pomodoro number
        constraintSet.connect(R.id.text_view_pomodoro_number,
                ConstraintSet.START,
                R.id.text_view_task,
                ConstraintSet.END);
        constraintSet.clear(R.id.text_view_pomodoro_number, ConstraintSet.END);

        // animate task
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        constraintSet.clear(R.id.text_view_task, ConstraintSet.END);

        // animate distractions
        constraintSet.setVisibility(R.id.text_view_daily_target, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_daily_target, ConstraintSet.INVISIBLE);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.INVISIBLE);

        constraintSet.applyTo(this);

    }

    @Override
    public boolean isStatusIdle() {
        return presenter.isStatusIdle();
    }


    //                                                                                       HELPERS

    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.view_progress_bottom_sheet, this);
        this.context = context;
        this.bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
        this.presenter = new ProgressProgressBottomSheetViewPresenter(this);
        this.constraintSet = new ConstraintSet();
    }

    public IProgressBottomSheetViewPresenter getPresenter() {
        return presenter;
    }

}

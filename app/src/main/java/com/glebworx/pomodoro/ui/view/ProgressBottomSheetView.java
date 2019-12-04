package com.glebworx.pomodoro.ui.view;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.NumberPickerManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.triggertrap.seekarc.SeekArc;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.glebworx.pomodoro.util.constants.Constants.MAX_POMODOROS_SESSION;
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
    @BindView(R.id.button_start_stop_skip)
    AppCompatImageButton startStopSkipButton;
    @BindView(R.id.button_sessions_remaining)
    AppCompatButton sessionsRemainingButton;
    @BindView(R.id.text_view_time_remaining_large) AppCompatTextView timeRemainingLargeTextView;
    @BindView(R.id.seek_arc) SeekArc seekArc;
    @BindView(R.id.fab_start_stop_skip_large)
    FloatingActionButton startStopSkipFab;
    @BindView(R.id.button_cancel) AppCompatImageButton cancelButton;
    @BindView(R.id.button_complete) AppCompatImageButton completeButton;
    @BindView(R.id.spin_kit_view_large)
    SpinKitView spinKitView;


    //                                                                                    ATTRIBUTES

    private Context context;
    private ConstraintSet constraintSet;
    private IProgressBottomSheetViewInteractionListener bottomSheetListener;
    private int bottomSheetState;
    private Unbinder unbinder;
    private ProgressProgressBottomSheetViewPresenter presenter;


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
            startStopSkipButton.setOnClickListener(this);
            startStopSkipFab.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
            completeButton.setOnClickListener(this);
            sessionsRemainingButton.setOnClickListener(this);
        } else {
            bottomSheetListener = null;
            startStopSkipButton.setOnClickListener(null);
            startStopSkipFab.setOnClickListener(null);
            cancelButton.setOnClickListener(null);
            completeButton.setOnClickListener(null);
            sessionsRemainingButton.setOnClickListener(null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.destroy();
        super.onDetachedFromWindow();
        unbinder.unbind();
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_stop_skip:
            case R.id.fab_start_stop_skip_large:
                presenter.handleStartStopSkipClick();
                break;
            case R.id.button_cancel:
                presenter.cancelSession((MainActivity) context);
                break;
            case R.id.button_complete:
                presenter.completeTask((MainActivity) context);
                break;
            case R.id.button_sessions_remaining:
                showSessionCountDialog();
                break;
        }
    }

    @Override
    public synchronized void onTaskSet(String name, int totalPomodoroCount) {
        taskTextView.setText(name);
        statusTextView.setText(R.string.bottom_sheet_text_status_idle);
        String timeRemainingString = DateTimeManager.formatMMSSString(context, POMODORO_LENGTH * 60);
        timeRemainingLargeTextView.setText(timeRemainingString);
        timeRemainingTextView.setText(timeRemainingString);
        updatePomodoroCountText(0, totalPomodoroCount);
        spinKitView.setVisibility(INVISIBLE);
    }

    @Override
    public synchronized void onPomodoroCountChanged(int completedPomodoroCount, int totalPomodoroCount) {
        updatePomodoroCountText(completedPomodoroCount, totalPomodoroCount);
    }

    @Override
    public void onTaskStarted() {
        startStopSkipButton.setImageResource(R.drawable.ic_pause_highlight);
        startStopSkipFab.setImageResource(R.drawable.ic_pause_black);
        statusTextView.setText(R.string.bottom_sheet_text_status_active);
        cancelButton.setVisibility(VISIBLE);
        completeButton.setVisibility(VISIBLE);
        spinKitView.setVisibility(VISIBLE);
    }

    @Override
    public void onTaskResumed() {
        startStopSkipButton.setImageResource(R.drawable.ic_pause_highlight);
        startStopSkipFab.setImageResource(R.drawable.ic_pause_black);
        statusTextView.setText(R.string.bottom_sheet_text_status_active);
        spinKitView.setVisibility(VISIBLE);
    }

    @Override
    public void onTaskPaused() {
        startStopSkipButton.setImageResource(R.drawable.ic_play_highlight);
        startStopSkipFab.setImageResource(R.drawable.ic_play_black);
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

    @Override
    public void onRestingPeriodStarted() {
        startStopSkipButton.setImageResource(R.drawable.ic_next_highlight);
        startStopSkipFab.setImageResource(R.drawable.ic_next_black);
        statusTextView.setText(R.string.bottom_sheet_text_status_resting);
        cancelButton.setVisibility(INVISIBLE);
        completeButton.setVisibility(VISIBLE);
        spinKitView.setVisibility(VISIBLE);
    }

    @Override
    public void onPomodoroCompleted(boolean isSuccessful, int totalSessions, int completedSessions) {
        updatePomodoroCountText(completedSessions, totalSessions);
        Toast.makeText(
                context,
                isSuccessful
                        ? R.string.bottom_sheet_toast_pomodoro_completed_success
                        : R.string.bottom_sheet_toast_pomodoro_completed_failed,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskCompleted(boolean isSuccessful) {
        Toast.makeText(
                context,
                isSuccessful
                        ? R.string.bottom_sheet_toast_task_completed_success
                        : R.string.bottom_sheet_toast_task_completed_failed,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskDataChanged(int pomodorosAllocated, int pomodorosCompleted) {
        pomodoroNumberTextView.setText(context.getString(
                R.string.core_ratio,
                String.valueOf(pomodorosCompleted),
                String.valueOf(pomodorosAllocated)));
    }

    @Override
    public synchronized void onTick(long millisUntilFinished, int progress) {

        String minutesUntilFinished = DateTimeManager.formatMMSSString(context, (int) (millisUntilFinished / 1000));

        if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
            timeRemainingLargeTextView.setText(minutesUntilFinished);
            if (seekArc.getProgress() != progress) {
                seekArc.setProgress(progress);
            }
        } else if (bottomSheetState == BottomSheetBehavior.STATE_COLLAPSED) {
            timeRemainingTextView.setText(minutesUntilFinished);
            if (progressBar.getProgress() != progress) {
                progressBar.setProgress(progress);
            }
        }

    }

    @Override
    public synchronized void onClearViews() {
        startStopSkipButton.setImageResource(R.drawable.ic_play_highlight);
        startStopSkipFab.setImageResource(R.drawable.ic_play_black);
        statusTextView.setText(R.string.bottom_sheet_text_status_idle);
        timeRemainingLargeTextView.setText(null);
        timeRemainingTextView.setText(null);
        seekArc.setProgress(0);
        progressBar.setProgress(0);
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

        // animate pomodoro number and remaining sessions
        constraintSet.connect(R.id.text_view_pomodoro_number,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START);
        constraintSet.connect(R.id.text_view_pomodoro_number,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END);
        constraintSet.setVisibility(R.id.button_sessions_remaining, ConstraintSet.VISIBLE);

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

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop_skip, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_skip_large, ConstraintSet.VISIBLE);
        if (presenter.isStatusResting()) {
            constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.INVISIBLE);
            constraintSet.setVisibility(R.id.button_complete, ConstraintSet.INVISIBLE);
        } else {
            constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.VISIBLE);
            constraintSet.setVisibility(R.id.button_complete, ConstraintSet.VISIBLE);
        }

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

        // animate pomodoro number and remaining sessions
        constraintSet.connect(R.id.text_view_pomodoro_number,
                ConstraintSet.START,
                R.id.text_view_task,
                ConstraintSet.END);
        constraintSet.clear(R.id.text_view_pomodoro_number, ConstraintSet.END);
        constraintSet.setVisibility(R.id.button_sessions_remaining, ConstraintSet.INVISIBLE);

        // animate task
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        constraintSet.clear(R.id.text_view_task, ConstraintSet.END);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop_skip, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_skip_large, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.INVISIBLE);

        constraintSet.applyTo(this);

    }

    @Override
    public IProgressBottomSheetViewPresenter getPresenter() {
        return presenter;
    }


    //                                                                                       HELPERS

    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.view_progress_bottom_sheet, this);
        this.context = context;
        this.bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
        this.presenter = new ProgressProgressBottomSheetViewPresenter(this, context);
        this.constraintSet = new ConstraintSet();
    }

    private void updatePomodoroCountText(int completedPomodoroCount, int totalPomodoroCount) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        sessionsRemainingButton.setText(
                context.getString(R.string.bottom_sheet_title_pomodoro_count,
                        numberFormat.format(completedPomodoroCount),
                        numberFormat.format(totalPomodoroCount)));
    }

    private void showSessionCountDialog() {

        if (!(context instanceof Activity)) {
            return;
        }

        AlertDialog alertDialog = DialogManager.showDialog(
                (Activity) context,
                R.id.container_main,
                R.layout.dialog_set_task);

        int initialPomodoroCount = presenter.getCompletedPomodoroCount();
        if (initialPomodoroCount < 1) {
            initialPomodoroCount = 1;
        } else if (initialPomodoroCount > MAX_POMODOROS_SESSION) {
            initialPomodoroCount = MAX_POMODOROS_SESSION;
        }
        NumberPicker picker = alertDialog.findViewById(R.id.number_picker);
        NumberPickerManager.initPicker(context, Objects.requireNonNull(picker), initialPomodoroCount, MAX_POMODOROS_SESSION);

        AppCompatButton positiveButton = alertDialog.findViewById(R.id.button_positive);

        ((AppCompatTextView) Objects.requireNonNull(
                alertDialog.findViewById(R.id.text_view_title))).setText(R.string.bottom_sheet_title_change_pomodoro_count);
        Spanned description = Html.fromHtml(
                context.getString(R.string.bottom_sheet_text_change_pomodoro_count, presenter.getTaskName()), 0);
        ((AppCompatTextView) Objects.requireNonNull(
                alertDialog.findViewById(R.id.text_view_description))).setText(description);
        Objects.requireNonNull(positiveButton).setText(R.string.main_title_replace_task);

        picker.setValue(initialPomodoroCount);

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_positive) {
                presenter.changePomodoroCount(picker.getValue());
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            }
        };

        ((AppCompatButton) Objects.requireNonNull(alertDialog.findViewById(R.id.button_negative))).setOnClickListener(onClickListener);
        Objects.requireNonNull(positiveButton).setOnClickListener(onClickListener);

    }

}

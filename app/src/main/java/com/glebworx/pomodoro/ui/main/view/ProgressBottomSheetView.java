package com.glebworx.pomodoro.ui.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.PomodoroTimer;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.triggertrap.seekarc.SeekArc;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;


public class ProgressBottomSheetView extends ConstraintLayout implements View.OnClickListener {

    @BindView(R.id.progress_bar) MaterialProgressBar progressBar;
    @BindView(R.id.text_view_task) AppCompatTextView taskTextView;
    @BindView(R.id.text_view_pomodoro_number) AppCompatTextView pomodoroNumberTextView;
    @BindView(R.id.text_view_status) AppCompatTextView statusTextView;
    @BindView(R.id.text_view_time_remaining) AppCompatTextView timeRemainingTextView;
    @BindView(R.id.button_start_stop) AppCompatImageButton startStopButton;
    @BindView(R.id.text_view_distractions_count) AppCompatTextView distractionsCountTextView;
    @BindView(R.id.text_view_distractions) AppCompatTextView distractionsTextView;
    @BindView(R.id.text_view_time_remaining_large) AppCompatTextView timeRemainingLargeTextView;
    @BindView(R.id.seek_arc) SeekArc seekArc;
    @BindView(R.id.fab_start_stop_large) FloatingActionButton startStopFab;
    @BindView(R.id.button_cancel) AppCompatImageButton cancelButton;
    @BindView(R.id.button_complete) AppCompatImageButton completeButton;

    public static final int PROGRESS_STATUS_IDLE = 0;
    public static final int PROGRESS_STATUS_PAUSED = 1;
    public static final int PROGRESS_STATUS_ACTIVE = 2;

    private ConstraintSet constraintSet;
    private OnBottomSheetInteractionListener bottomSheetListener;
    private PomodoroTimer timer;
    private Unbinder unbinder;
    private int bottomSheetState;
    private int progressStatus;
    private TaskModel taskModel;
    private final Object object = new Object();

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

    private void init(Context context, AttributeSet attrs, int defStyle) {

        inflate(context, R.layout.view_progress_bottom_sheet, this);

        bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
        constraintSet = new ConstraintSet();

        initTimer();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bottomSheetListener = (OnBottomSheetInteractionListener) getContext();
        startStopButton.setOnClickListener(this);
        startStopFab.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        completeButton.setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bottomSheetListener = null;
        startStopButton.setOnClickListener(null);
        startStopFab.setOnClickListener(null);
        cancelButton.setOnClickListener(null);
        completeButton.setOnClickListener(null);
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_stop:
            case R.id.fab_start_stop_large:
                if (progressStatus == PROGRESS_STATUS_ACTIVE) {
                    pauseTask();
                } else {
                    startTask();
                }
                break;
            case R.id.button_cancel:
                cancelTask();
                break;
            case R.id.button_complete:
                break;
        }
    }

    private void startTask() {
        synchronized (object) {
            if (progressStatus == PROGRESS_STATUS_IDLE) {
                timer.cancel();
                initTimer();
                timer.start();
            } else {
                timer.resume();
            }
            progressStatus = PROGRESS_STATUS_ACTIVE;
            startStopButton.setImageResource(R.drawable.ic_pause_highlight);
            startStopFab.setImageResource(R.drawable.ic_pause_black);
            statusTextView.setText(R.string.main_text_status_active);
        }
        //startStopFab.setIco
    }

    private void pauseTask() {
        synchronized (object) {
            timer.pause();
            progressStatus = PROGRESS_STATUS_PAUSED;
            startStopButton.setImageResource(R.drawable.ic_play_highlight);
            startStopFab.setImageResource(R.drawable.ic_play_black);
            statusTextView.setText(R.string.main_text_status_paused);
        }
    }

    private void cancelTask() {
        //DialogManager.showDialog(getContext())
        synchronized (object) {
            timer.cancel();
            clearViews();
        }
        bottomSheetListener.onCancelTask(taskModel);
    }

    public void setTask(TaskModel taskModel) {
        synchronized (object) {
            this.taskModel = taskModel;
            taskTextView.setText(taskModel.getName());
            statusTextView.setText(R.string.main_text_status_idle);
            String timeRemainingString = DateTimeManager.formatMMSSString(getContext(), POMODORO_LENGTH * 60);
            timeRemainingLargeTextView.setText(timeRemainingString);
            timeRemainingTextView.setText(timeRemainingString);
        }
    }

    public void expandBottomSheetViews() {

        this.bottomSheetState = BottomSheetBehavior.STATE_EXPANDED;

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
        constraintSet.setVisibility(R.id.text_view_distractions, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.text_view_distractions_count, ConstraintSet.VISIBLE);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.VISIBLE);

        constraintSet.applyTo(this);

    }

    public void collapseBottomSheetViews() {

        this.bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;

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
        constraintSet.setVisibility(R.id.text_view_distractions, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.text_view_distractions_count, ConstraintSet.INVISIBLE);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.INVISIBLE);

        constraintSet.applyTo(this);

    }

    public int getProgressStatus() {
        return progressStatus;
    }

    private void initTimer() {

        final String[] minutesUntilFinished = new String[1];
        int duration = POMODORO_LENGTH * 60000;
        int durationPercent = duration / 100;
        final int[] progress = {0};

        timer = new PomodoroTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                minutesUntilFinished[0] = DateTimeManager.formatMMSSString(getContext(), (int) (millisUntilFinished / 1000));
                progress[0] = 100 - (int) (millisUntilFinished / durationPercent);

                if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
                    synchronized (object) {
                        timeRemainingLargeTextView.setText(minutesUntilFinished[0]);
                        if (seekArc.getProgress() != progress[0]) {
                            seekArc.setProgress(progress[0]);
                        }
                    }
                } else if (bottomSheetState == BottomSheetBehavior.STATE_COLLAPSED) {
                    synchronized (object) {
                        timeRemainingTextView.setText(minutesUntilFinished[0]);
                        if (progressBar.getProgress() != progress[0]) {
                            progressBar.setProgress(progress[0]);
                        }
                    }
                }

            }

            @Override
            public void onFinish() {
                clearViews();
            }
        };

    }

    private void clearViews() {
        progressStatus = PROGRESS_STATUS_IDLE;
        startStopButton.setImageResource(R.drawable.ic_play_highlight);
        startStopFab.setImageResource(R.drawable.ic_play_black);
        statusTextView.setText(R.string.main_text_status_idle);
        timeRemainingLargeTextView.setText(null);
        timeRemainingTextView.setText(null);
        seekArc.setProgress(0);
        progressBar.setProgress(0);
    }

    public interface OnBottomSheetInteractionListener {
        void onCancelTask(TaskModel taskModel);
        void onCompleteTask(TaskModel taskModel);
    }

}

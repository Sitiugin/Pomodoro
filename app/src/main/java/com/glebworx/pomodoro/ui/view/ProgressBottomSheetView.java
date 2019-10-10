package com.glebworx.pomodoro.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.view.interfaces.IBottomSheetViewInteractionListener;
import com.glebworx.pomodoro.ui.view.interfaces.IBottomSheetViewPresenter;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.glebworx.pomodoro.util.PomodoroTimer;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
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
    @BindView(R.id.text_view_distractions_count) AppCompatTextView distractionsCountTextView;
    @BindView(R.id.text_view_distractions) AppCompatTextView distractionsTextView;
    @BindView(R.id.text_view_time_remaining_large) AppCompatTextView timeRemainingLargeTextView;
    @BindView(R.id.seek_arc) SeekArc seekArc;
    @BindView(R.id.fab_start_stop_large) FloatingActionButton startStopFab;
    @BindView(R.id.button_cancel) AppCompatImageButton cancelButton;
    @BindView(R.id.button_complete) AppCompatImageButton completeButton;


    //                                                                                    ATTRIBUTES

    private ConstraintSet constraintSet;
    private IBottomSheetViewInteractionListener bottomSheetListener;
    private PomodoroTimer timer;
    private int bottomSheetState;
    private Unbinder unbinder;
    private ProgressBottomSheetViewPresenter presenter;
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

    /*@Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }*/

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        bottomSheetListener = (IBottomSheetViewInteractionListener) getContext();
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


    //                                                                                IMPLEMENTATION


    @Override
    public void onInitView() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_stop:
            case R.id.fab_start_stop_large:
                presenter.handleStartStopClick();
                break;
            case R.id.button_cancel:
                presenter.cancelTask();
                break;
            case R.id.button_complete:
                presenter.completeTask();
                break;
        }
    }

    @Override
    public void onTaskSet(String name) {
        synchronized (object) {
            taskTextView.setText(name);
            statusTextView.setText(R.string.main_text_status_idle);
            String timeRemainingString = DateTimeManager.formatMMSSString(getContext(), POMODORO_LENGTH * 60);
            timeRemainingLargeTextView.setText(timeRemainingString);
            timeRemainingTextView.setText(timeRemainingString);
        }
    }

    @Override
    public void onStartTask() {
        synchronized (object) {
            timer.cancel();
            initTimer();
            timer.start();
            startStopButton.setImageResource(R.drawable.ic_pause_highlight);
            startStopFab.setImageResource(R.drawable.ic_pause_black);
            statusTextView.setText(R.string.main_text_status_active);
        }
    }

    @Override
    public void onResumeTask() {
        synchronized (object) {
            timer.resume();
            startStopButton.setImageResource(R.drawable.ic_pause_highlight);
            startStopFab.setImageResource(R.drawable.ic_pause_black);
            statusTextView.setText(R.string.main_text_status_active);
        }
    }

    @Override
    public void onPauseTask() {
        synchronized (object) {
            timer.pause();
            startStopButton.setImageResource(R.drawable.ic_play_highlight);
            startStopFab.setImageResource(R.drawable.ic_play_black);
            statusTextView.setText(R.string.main_text_status_paused);
            if (bottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
                timeRemainingTextView.setText(timeRemainingLargeTextView.getText());
                progressBar.setProgress(seekArc.getProgress());
            } else if (bottomSheetState == BottomSheetBehavior.STATE_COLLAPSED) {
                timeRemainingLargeTextView.setText(timeRemainingTextView.getText());
                seekArc.setProgress(progressBar.getProgress());
            }
        }
    }

    @Override
    public void onTaskCanceled() {
        //DialogManager.showDialog(getContext())
        synchronized (object) {
            timer.cancel();
            clearViews();
        }
        bottomSheetListener.onHideBottomSheet();
    }

    @Override
    public void onTaskCompleted() {
        synchronized (object) {
            timer.cancel();
            clearViews();
        }
        bottomSheetListener.onHideBottomSheet();
    }

    @Override
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

    @Override
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


    //                                                                                       HELPERS

    private void init(Context context, AttributeSet attrs, int defStyle) {
        inflate(context, R.layout.view_progress_bottom_sheet, this);
        bottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
        presenter = new ProgressBottomSheetViewPresenter(this);
        constraintSet = new ConstraintSet();
        initTimer();
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
        startStopButton.setImageResource(R.drawable.ic_play_highlight);
        startStopFab.setImageResource(R.drawable.ic_play_black);
        statusTextView.setText(R.string.main_text_status_idle);
        timeRemainingLargeTextView.setText(null);
        timeRemainingTextView.setText(null);
        seekArc.setProgress(0);
        progressBar.setProgress(0);
    }

    public IBottomSheetViewPresenter getPresenter() {
        return presenter;
    }

}

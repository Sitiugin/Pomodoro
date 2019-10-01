package com.glebworx.pomodoro.ui.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import android.os.Bundle;
import android.view.View;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.main.fragment.AddProjectFragment;
import com.glebworx.pomodoro.ui.main.fragment.AddTaskFragment;
import com.glebworx.pomodoro.ui.main.fragment.ProjectsFragment;
import com.glebworx.pomodoro.ui.main.fragment.ReportFragment;
import com.glebworx.pomodoro.ui.main.fragment.ViewProjectFragment;
import com.glebworx.pomodoro.util.PomodoroTimer;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.TransitionFragmentManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.triggertrap.seekarc.SeekArc;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;


public class MainActivity
        extends AppCompatActivity
        implements ProjectsFragment.OnProjectFragmentInteractionListener,
                    AddProjectFragment.OnAddProjectFragmentInteractionListener,
                    ViewProjectFragment.OnViewProjectFragmentInteractionListener,
                    AddTaskFragment.OnAddTaskFragmentInteractionListener {


    //                                                                                       BINDING

    @BindView(R.id.bottom_sheet) ConstraintLayout bottomSheet;
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

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_PAUSED = 0;
    private static final int STATUS_IDLE = -1;

    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintSet constraintSet;
    private TransitionFragmentManager fragmentManager;
    private PomodoroTimer countDownTimer;
    private TaskModel activeTask;
    private int status;

    //                                                                                     LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        constraintSet = new ConstraintSet();
        fragmentManager =
                new TransitionFragmentManager(getSupportFragmentManager(), R.id.container_main);

        initBottomSheet();
        initClickEvents();
        addProjectsFragment();

    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (fragmentManager.hasStackedFragments()){
            fragmentManager.popFromBackStack();
        } else {
            super.onBackPressed();
        }
    }


    //                                                                          NAVIGATION CALLBACKS

    @Override
    public void onAddProject() {
        fragmentManager.pushToBackStack(AddProjectFragment.newInstance());
    }

    @Override
    public void onViewProject(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(ViewProjectFragment.newInstance(projectModel));
    }

    @Override
    public void onViewReport() {
        fragmentManager.pushToBackStack(ReportFragment.newInstance());
    }

    @Override
    public void onCloseFragment() {
        fragmentManager.popFromBackStack();
    }

    @Override
    public void onEditProject(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(AddProjectFragment.newInstance(projectModel));
    }

    @Override
    public void onAddTask(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(AddTaskFragment.newInstance(projectModel));
    }

    @Override
    public void onSelectTask(TaskModel taskModel) {
        this.activeTask = taskModel;

        bottomSheet.setVisibility(View.VISIBLE);
        attachTaskToBottomSheet(taskModel);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onEditTask(ProjectModel projectModel, TaskModel taskModel) {
        fragmentManager.pushToBackStack(AddTaskFragment.newInstance(projectModel, taskModel));
    }


    //                                                                                       HELPERS

    private void initBottomSheet() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    expandBottomSheetViews();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    collapseBottomSheetViews();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) { }
        });
    }

    private void attachTaskToBottomSheet(TaskModel taskModel) {
        if (countDownTimer != null) {
            //countDownTimer.cancel();
            countDownTimer.onFinish();
        }
        final String[] minutesUntilFinished = new String[1];
        int duration = POMODORO_LENGTH * 60000;
        int durationPercent = duration / 100;
        countDownTimer = new PomodoroTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                minutesUntilFinished[0] = DateTimeManager.formatMMSSString(MainActivity.this, (int) (millisUntilFinished / 1000));
                timeRemainingTextView.setText(minutesUntilFinished[0]);
                timeRemainingLargeTextView.setText(minutesUntilFinished[0]);
                seekArc.setProgress(100 - (int) (millisUntilFinished / durationPercent));
            }

            @Override
            public void onFinish() { }
        };
    }

    private void expandBottomSheetViews() {

        TransitionManager.beginDelayedTransition(bottomSheet);
        constraintSet.clone(bottomSheet);

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

        // animate distractions
        constraintSet.setVisibility(R.id.text_view_distractions, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.text_view_distractions_count, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.text_view_pomodoro_number, ConstraintSet.VISIBLE);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.VISIBLE);

        constraintSet.applyTo(bottomSheet);

    }

    private void collapseBottomSheetViews() {

        TransitionManager.beginDelayedTransition(bottomSheet);
        constraintSet.clone(bottomSheet);

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

        // animate task
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        constraintSet.clear(R.id.text_view_task, ConstraintSet.END);

        // animate distractions & pomodoro number
        constraintSet.setVisibility(R.id.text_view_distractions, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.text_view_distractions_count, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.text_view_pomodoro_number, ConstraintSet.INVISIBLE);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.INVISIBLE);

        constraintSet.applyTo(bottomSheet);

    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.bottom_sheet:
                    onBottomSheetClick();
                    break;
                case R.id.fab_start_stop_large:
                    onStartStopLargeClick();
                    break;
            }
        };
        bottomSheet.setOnClickListener(onClickListener);
        startStopFab.setOnClickListener(onClickListener);
    }

    private void onBottomSheetClick() {
        int state = bottomSheetBehavior.getState();
        if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void onStartStopLargeClick() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer.start();
        }
    }

    private void addProjectsFragment() {
        fragmentManager.addRootFragment(ProjectsFragment.newInstance());
    }

}

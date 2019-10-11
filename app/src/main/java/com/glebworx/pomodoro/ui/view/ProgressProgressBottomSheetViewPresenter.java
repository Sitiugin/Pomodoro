package com.glebworx.pomodoro.ui.view;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewPresenter;
import com.glebworx.pomodoro.util.PomodoroTimer;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;

public class ProgressProgressBottomSheetViewPresenter implements IProgressBottomSheetViewPresenter {


    //                                                                                     CONSTANTS

    private static final int PROGRESS_STATUS_IDLE = 0;
    private static final int PROGRESS_STATUS_PAUSED = 1;
    private static final int PROGRESS_STATUS_ACTIVE = 2;

    private IProgressBottomSheetView presenterListener;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private PomodoroTimer timer;
    private int progressStatus;

    ProgressProgressBottomSheetViewPresenter(@NonNull IProgressBottomSheetView presenterListener) {
        this.presenterListener = presenterListener;
        init();
        initTimer();
    }


    @Override
    public void init() {
        progressStatus = PROGRESS_STATUS_IDLE;
    }

    @Override
    public void setTask(ProjectModel projectModel, TaskModel taskModel) {
        progressStatus = PROGRESS_STATUS_IDLE;
        this.projectModel = projectModel;
        this.taskModel = taskModel;
        presenterListener.onTaskSet(taskModel.getName());
    }

    @Override
    public void handleStartStopClick() {
        if (progressStatus == PROGRESS_STATUS_ACTIVE) {
            progressStatus = PROGRESS_STATUS_PAUSED;
            timer.pause();
            presenterListener.onTaskPaused();
        } else if (progressStatus == PROGRESS_STATUS_IDLE){
            progressStatus = PROGRESS_STATUS_ACTIVE;
            timer.cancel();
            initTimer();
            timer.start();
            presenterListener.onTaskStarted();
        } else {
            progressStatus = PROGRESS_STATUS_ACTIVE;
            timer.resume();
            presenterListener.onTaskResumed();
        }
    }

    @Override
    public void cancelTask() {
        progressStatus = PROGRESS_STATUS_IDLE;
        presenterListener.onClearViews();
        presenterListener.onHideBottomSheet();
    }

    @Override
    public void completeTask() {
        completePomodoro();
    }

    private void initTimer() {
        timer = new PomodoroTimer(POMODORO_LENGTH * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                presenterListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                completePomodoro();
            }
        };
    }

    private void completePomodoro() {
        progressStatus = PROGRESS_STATUS_IDLE;
        presenterListener.onClearViews();
        if (taskModel == null) {
            return;
        }
        taskModel.addPomodoro();
        TaskApi.addTask(projectModel, taskModel, task -> {
            presenterListener.onPomodoroCompleted(task.isSuccessful());
        });
    }

}

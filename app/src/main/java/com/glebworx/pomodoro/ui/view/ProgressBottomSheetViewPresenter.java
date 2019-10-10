package com.glebworx.pomodoro.ui.view;

import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.view.interfaces.IBottomSheetViewPresenter;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ProgressBottomSheetViewPresenter implements IBottomSheetViewPresenter {


    //                                                                                     CONSTANTS

    public static final int PROGRESS_STATUS_IDLE = 0;
    public static final int PROGRESS_STATUS_PAUSED = 1;
    public static final int PROGRESS_STATUS_ACTIVE = 2;

    private IProgressBottomSheetView presenterListener;
    private TaskModel taskModel;
    private int progressStatus;

    public ProgressBottomSheetViewPresenter(IProgressBottomSheetView presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }


    @Override
    public void init() {
        progressStatus = PROGRESS_STATUS_IDLE;
        presenterListener.onInitView();
    }

    @Override
    public void setTask(TaskModel taskModel) {
        progressStatus = PROGRESS_STATUS_IDLE;
        this.taskModel = taskModel;
        presenterListener.onTaskSet(taskModel.getName());
    }

    @Override
    public void handleStartStopClick() {
        if (progressStatus == PROGRESS_STATUS_ACTIVE) {
            progressStatus = PROGRESS_STATUS_PAUSED;
            presenterListener.onPauseTask();
        } else if (progressStatus == PROGRESS_STATUS_IDLE){
            progressStatus = PROGRESS_STATUS_ACTIVE;
            presenterListener.onStartTask();
        } else {
            progressStatus = PROGRESS_STATUS_ACTIVE;
            presenterListener.onResumeTask();
        }
    }

    @Override
    public void cancelTask() {
        progressStatus = PROGRESS_STATUS_IDLE;
        presenterListener.onTaskCanceled();
    }

    @Override
    public void completeTask() {
        progressStatus = PROGRESS_STATUS_IDLE;
        presenterListener.onTaskCompleted();
    }
}

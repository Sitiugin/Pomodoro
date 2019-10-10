package com.glebworx.pomodoro.ui.view.interfaces;

import com.glebworx.pomodoro.model.TaskModel;

public interface IProgressBottomSheetViewPresenter {

    void init();
    void setTask(TaskModel taskModel);
    void handleStartStopClick();
    void cancelTask();
    void completeTask();

}

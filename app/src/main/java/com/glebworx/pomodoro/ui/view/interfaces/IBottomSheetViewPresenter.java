package com.glebworx.pomodoro.ui.view.interfaces;

import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.view.ProgressBottomSheetViewPresenter;

public interface IBottomSheetViewPresenter {

    void init();
    void setTask(TaskModel taskModel);
    void handleStartStopClick();
    void cancelTask();
    void completeTask();

}

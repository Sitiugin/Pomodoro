package com.glebworx.pomodoro.ui.view.interfaces;

import com.glebworx.pomodoro.model.TaskModel;

public interface IBottomSheetViewInteractionListener {

    void onCancelTask(TaskModel taskModel);
    void onCompleteTask(TaskModel taskModel);

}

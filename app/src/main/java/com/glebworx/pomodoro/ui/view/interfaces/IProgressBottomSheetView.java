package com.glebworx.pomodoro.ui.view.interfaces;

import com.glebworx.pomodoro.model.TaskModel;

public interface IProgressBottomSheetView {

    void onInitView();
    void onTaskSet(String name);
    void onStartTask();
    void onResumeTask();
    void onPauseTask();
    void onTaskCanceled();
    void onTaskCompleted();
    void expandBottomSheetViews();
    void collapseBottomSheetViews();

}

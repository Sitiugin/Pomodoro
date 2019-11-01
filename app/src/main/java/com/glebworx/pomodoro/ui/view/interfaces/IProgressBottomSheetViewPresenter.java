package com.glebworx.pomodoro.ui.view.interfaces;

import android.app.Activity;
import android.content.Context;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

public interface IProgressBottomSheetViewPresenter {

    void init();

    void setTask(ProjectModel projectModel, TaskModel taskModel);

    void handleStartStopClick();

    void cancelSession(Activity activity);

    void completeTask(Activity activity);

    void showDailyTargetDialog(Context context);

    boolean isStatusIdle();
}

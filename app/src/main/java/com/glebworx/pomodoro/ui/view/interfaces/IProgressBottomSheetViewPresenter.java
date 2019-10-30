package com.glebworx.pomodoro.ui.view.interfaces;

import android.content.Context;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

public interface IProgressBottomSheetViewPresenter {

    void init();

    void subscribe();

    void unsubscribe();

    void setTask(ProjectModel projectModel, TaskModel taskModel);

    void handleStartStopClick();

    void cancelTask();

    void completeTask();

    void showDailyTargetDialog(Context context);

}

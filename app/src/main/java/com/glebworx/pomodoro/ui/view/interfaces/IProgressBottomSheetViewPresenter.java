package com.glebworx.pomodoro.ui.view.interfaces;

import android.content.Context;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

public interface IProgressBottomSheetViewPresenter {

    void init(Context context);

    void destroy();

    void setTask(ProjectModel projectModel, TaskModel taskModel, int numberOfSessions);

    void changePomodoroCount(int newPomodoroCount);

    void handleStartStopSkipClick();

    void closeSession();

    void completeTask();

    boolean isStatusIdle();

    boolean isStatusResting();

    boolean hasTask();

    String getTaskName();

    int getTotalPomodoroCount();

    int getCompletedPomodoroCount();

    int getRemainingPomodoroCount();

    void clearNotifications();
}

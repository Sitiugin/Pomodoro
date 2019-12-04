package com.glebworx.pomodoro.ui.view.interfaces;

public interface IProgressBottomSheetView {

    void onTaskSet(String name, int totalPomodoroCount);

    void onPomodoroCountChanged(int completedPomodoroCount, int totalPomodoroCount);

    void onTaskStarted();

    void onTaskResumed();

    void onTaskPaused();

    void onRestingPeriodStarted();

    void onPomodoroCompleted(boolean isSuccessful, int totalSessions, int completedSessions);

    void onTaskCompleted(boolean isSuccessful);

    void onTaskDataChanged(int pomodorosAllocated, int pomodorosCompleted);

    void onTick(long millisUntilFinished, int progress);

    void onClearViews();

    void onHideBottomSheet();

    void expandBottomSheetViews();

    void collapseBottomSheetViews();

    IProgressBottomSheetViewPresenter getPresenter();

}

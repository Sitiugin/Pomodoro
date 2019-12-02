package com.glebworx.pomodoro.ui.view.interfaces;

public interface IProgressBottomSheetView {

    void onTaskSet(String name, int numberOfSessions);

    void onTaskStarted();

    void onTaskResumed();

    void onTaskPaused();

    void onPomodoroCompleted(boolean isSuccessful, int totalSessions, int completedSessions);

    void onTaskCompleted(boolean isSuccessful);

    void onTaskDataChanged(int pomodorosAllocated, int pomodorosCompleted);

    void onTick(long millisUntilFinished, int progress);

    void onClearViews();

    void onHideBottomSheet();

    void expandBottomSheetViews();

    void collapseBottomSheetViews();

}

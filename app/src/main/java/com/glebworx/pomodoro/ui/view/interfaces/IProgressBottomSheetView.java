package com.glebworx.pomodoro.ui.view.interfaces;

public interface IProgressBottomSheetView {

    void onTaskSet(String name);

    void onTaskStarted();

    void onTaskResumed();

    void onTaskPaused();

    void onPomodoroCompleted(boolean isSuccessful);

    void onTaskCompleted(boolean isSuccessful);

    void onTodayCountUpdated(int newCount);

    void onPomodoroTargetUpdated(int completedToday, int newTarget);

    void onTaskDataChanged(int pomodorosAllocated, int pomodorosCompleted);

    void onTick(long millisUntilFinished, int progress);

    void onClearViews();

    void onHideBottomSheet();

    void expandBottomSheetViews();

    void collapseBottomSheetViews();

}

package com.glebworx.pomodoro.ui.view.interfaces;

public interface IProgressBottomSheetView {

    void onTaskSet(String name, int pomodorosAllocated, int pomodorosCompleted);

    void onTaskStarted();

    void onTaskResumed();

    void onTaskPaused();

    void onPomodoroCompleted(boolean isSuccessful, int pomodorosAllocated, int pomodorosCompleted);

    void onTodayCountUpdated(int newCount);

    void onPomodoroTargetUpdated(int completedToday, int newTarget);

    void onTick(long millisUntilFinished);

    void onClearViews();

    void onHideBottomSheet();

    void expandBottomSheetViews();

    void collapseBottomSheetViews();

}

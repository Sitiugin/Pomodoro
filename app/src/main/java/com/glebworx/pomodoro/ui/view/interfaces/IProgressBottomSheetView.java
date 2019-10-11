package com.glebworx.pomodoro.ui.view.interfaces;

public interface IProgressBottomSheetView {

    void onTaskSet(String name);

    void onTaskStarted();

    void onTaskResumed();

    void onTaskPaused();

    void onPomodoroCompleted(boolean isSuccessful);
    void onTick(long millisUntilFinished);
    void onClearViews();
    void onHideBottomSheet();
    void expandBottomSheetViews();
    void collapseBottomSheetViews();

}

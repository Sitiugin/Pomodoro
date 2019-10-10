package com.glebworx.pomodoro.ui.view.interfaces;

public interface IProgressBottomSheetView {

    void onInitView();
    void onTaskSet(String name);
    void onStartTask();
    void onResumeTask();
    void onPauseTask();

    void onTick(long millisUntilFinished);

    void onClearViews();

    void onHideBottomSheet();
    void expandBottomSheetViews();
    void collapseBottomSheetViews();

}

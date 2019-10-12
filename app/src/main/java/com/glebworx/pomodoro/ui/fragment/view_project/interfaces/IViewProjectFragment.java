package com.glebworx.pomodoro.ui.fragment.view_project.interfaces;

import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;

import java.util.Date;

public interface IViewProjectFragment {

    void onInitView(String projectName, ViewProjectHeaderItem headerItem);

    void onProjectDeleted(boolean isSuccessful);

    void onTaskDeleted(boolean isSuccessful, int position);

    void onHeaderItemChanged(int estimatedTime, int elapsedTime, double progressRatio);

    void onSubtitleChanged(Date dueDate, Date today);
}

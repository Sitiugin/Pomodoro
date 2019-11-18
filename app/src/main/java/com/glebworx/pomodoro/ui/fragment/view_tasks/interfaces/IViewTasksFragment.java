package com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces;

import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;

import java.util.Date;

public interface IViewTasksFragment {

    void onInitView(String projectName,
                    ViewProjectHeaderItem headerItem);

    void onTaskAdded(TaskItem item);

    void onTaskModified(TaskItem item);

    void onTaskDeleted(TaskItem item);

    void onTaskCompleted(TaskItem item, CompletedTaskItem completedItem);

    void onTaskDeleted(boolean isSuccessful, int position);

    void onHeaderItemChanged(int estimatedTime, int elapsedTime, double progressRatio);

    void onSubtitleChanged(Date dueDate, Date today);

}

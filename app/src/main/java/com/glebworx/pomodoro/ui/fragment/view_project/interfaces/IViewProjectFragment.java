package com.glebworx.pomodoro.ui.fragment.view_project.interfaces;

import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;
import com.glebworx.pomodoro.ui.item.TaskItem;

import java.util.Date;

public interface IViewProjectFragment {

    void onInitView(String projectName,
                    boolean allTasksCompleted,
                    boolean isCompleted,
                    ViewProjectHeaderItem headerItem);

    void onTaskAdded(TaskItem item);

    void onTaskModified(TaskItem item);

    void onTaskDeleted(TaskItem item);

    void onTaskCompleted(CompletedTaskItem completedItem);

    void onProjectDeleted(boolean isSuccessful);

    void onTaskDeleted(boolean isSuccessful, int position);

    void onHeaderItemChanged(ViewProjectHeaderItem headerItem);

    void onAppBarChanged(Date dueDate, Date today);

    void onCompleteChanged(boolean allTasksCompleted, boolean isCompleted);

}

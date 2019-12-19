package com.glebworx.pomodoro.ui.fragment.view_project.interfaces;

import com.glebworx.pomodoro.ui.fragment.view_project.item.AddTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;
import com.glebworx.pomodoro.ui.item.TaskItem;

import java.util.Date;

public interface IViewProjectFragment {

    void onInitView(String projectName,
                    Date dueDate,
                    boolean allTasksCompleted,
                    boolean isCompleted,
                    ViewProjectHeaderItem headerItem,
                    AddTaskItem addTaskItem);

    void onTaskAdded(TaskItem item);

    void onTaskModified(TaskItem item);

    void onTaskDeleted(TaskItem item);

    void onTaskCompleted(CompletedTaskItem completedItem);

    void onProjectDeleted(boolean isSuccessful);

    void onTaskDeleted(boolean isSuccessful, int position);

    void onColorTagChanged();

    void onDueDateChanged(Date dueDate);

    void onSummaryChanged();

}

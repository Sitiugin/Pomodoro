package com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces;

import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;

public interface IViewTasksFragment {

    void onInitView(String type);

    void onTaskAdded(TaskItem item);

    void onTaskModified(TaskItem item);

    void onTaskDeleted(TaskItem item);

    void onTaskCompleted(CompletedTaskItem completedItem);

    void onProjectChanged(String projectName, String tagColor);

}

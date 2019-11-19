package com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces;

import android.os.Bundle;

import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;

public interface IViewTasksFragmentPresenter {

    void init(Bundle arguments);

    void destroy();

    void selectTask(TaskItem taskItem);

    void viewProject(String projectName);

}

package com.glebworx.pomodoro.ui.fragment.view_project.interfaces;

import android.os.Bundle;

import com.glebworx.pomodoro.ui.item.TaskItem;

public interface IViewProjectFragmentPresenter {

    void init(Bundle arguments);

    void destroy();

    void editProject();

    void deleteProject();

    void completeProject();

    void addTask();

    void editTask(TaskItem taskItem);

    void deleteTask(TaskItem taskItem, int position);

    void selectTask(TaskItem taskItem);

}

package com.glebworx.pomodoro.ui.fragment.view_project.interfaces;

import android.os.Bundle;
import android.view.View;

import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;

public interface IViewProjectFragmentPresenter {

    void init(Bundle arguments, View.OnClickListener headerClickListener);

    void editProject();

    void deleteProject();

    void addTask();

    void editTask(TaskItem taskItem);

    void deleteTask(TaskItem taskItem, int position);

    void selectTask(TaskItem taskItem);

}

package com.glebworx.pomodoro.ui.fragment.add_task;

import android.os.Bundle;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragmentPresenter;

public class AddTaskFragmentPresenter implements IAddTaskFragmentPresenter {

    private ProjectModel projectModel;
    private TaskModel taskModel;
    private boolean isEditing;

    @Override
    public void init(Bundle arguments) {

    }

    @Override
    public void editTaskName(String name) {

    }

    @Override
    public void editDueDate() {

    }

    @Override
    public void selectDueDate(int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void selectPomodorosAllocated() {

    }

    @Override
    public void selectRecurrence() {

    }

    @Override
    public void addTask() {

    }



}

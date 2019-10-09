package com.glebworx.pomodoro.ui.fragment.add_task;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragment;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragmentPresenter;
import com.glebworx.pomodoro.util.manager.DateTimeManager;

import java.util.Date;

import static com.glebworx.pomodoro.ui.fragment.add_task.AddTaskFragment.ARG_PROJECT_MODEL;
import static com.glebworx.pomodoro.ui.fragment.add_task.AddTaskFragment.ARG_TASK_MODEL;

public class AddTaskFragmentPresenter implements IAddTaskFragmentPresenter {


    //                                                                                    ATTRIBUTES

    private IAddTaskFragment presenterListener;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private TaskModel oldTaskModel;
    private boolean isEditing;


    //                                                                                  CONSTRUCTORS

    AddTaskFragmentPresenter(@NonNull IAddTaskFragment presenterListener,
                             @Nullable Bundle arguments) {
        this.presenterListener = presenterListener;
        init(arguments);
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void init(Bundle arguments) {

        if (arguments != null) {
            projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
            taskModel = arguments.getParcelable(ARG_TASK_MODEL);
        }

        if (taskModel == null) {
            isEditing = false;
            taskModel = new TaskModel();
            taskModel.setDueDate(new Date());
        } else {
            isEditing = true;
            oldTaskModel = new TaskModel(taskModel);
        }

        presenterListener.onInitView(
                isEditing,
                taskModel.getName(),
                DateTimeManager.getDateString(taskModel.getDueDate(), new Date()),
                taskModel.getPomodorosAllocated(),
                taskModel.getRecurrence());

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

    //                                                                                       HELPERS

}

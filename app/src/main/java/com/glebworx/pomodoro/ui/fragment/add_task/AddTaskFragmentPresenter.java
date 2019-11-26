package com.glebworx.pomodoro.ui.fragment.add_task;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragment;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragmentPresenter;
import com.glebworx.pomodoro.util.manager.DateTimeManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
            taskModel = new TaskModel(projectModel.getName());
            taskModel.setDueDate(new Date());
            taskModel.setPomodorosAllocated(1);
        } else {
            isEditing = true;
            oldTaskModel = new TaskModel(taskModel);
        }

        presenterListener.onInitView(
                isEditing,
                taskModel.getName(),
                DateTimeManager.getDateString(taskModel.getDueDate(), new Date()),
                taskModel.getPomodorosAllocated());

    }

    @Override
    public void editTaskName(String name) {
        if (!isEditing) {
            taskModel.setName(name);
            presenterListener.onTaskNameChanged();
        }
    }

    @Override
    public void editDueDate() {
        presenterListener.onEditDueDate(taskModel.getDueDate());
    }

    @Override
    public void editPomodorosAllocated() {
        presenterListener.onEditPomodorosAllocated(taskModel.getPomodorosAllocated());
    }

    @Override
    public void selectDueDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        taskModel.setDueDate(calendar.getTime());
        presenterListener.onSelectDueDate(DateTimeManager.getDateString(taskModel.getDueDate(), new Date()));
    }

    @Override
    public void selectPomodorosAllocated(int pomodorosAllocated) {
        taskModel.setPomodorosAllocated(pomodorosAllocated);
        presenterListener.onPomodorosChanged(pomodorosAllocated);
    }

    @Override
    public void saveTask() {

        if (taskModel.isValid()) {

            if (isEditing) {
                TaskApi.updateTask(projectModel, taskModel, null);
            } else {
                TaskApi.addTask(projectModel, taskModel, null);
            }

            presenterListener.onAddTask(isEditing);

        } else {

            presenterListener.onTaskValidationFailed(
                    taskModel.getName() == null
                            || taskModel.getName().isEmpty());

        }

    }

}

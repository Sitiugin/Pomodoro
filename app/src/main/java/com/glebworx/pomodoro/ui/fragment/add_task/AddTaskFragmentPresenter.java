package com.glebworx.pomodoro.ui.fragment.add_task;

import android.os.Bundle;
import android.util.SparseArray;

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

import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_DAY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_FIVE_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_FOUR_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_SIX_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_THREE_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_TWO_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_WEEKLY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_MONTHLY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_WEEKDAY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_WEEKEND;
import static com.glebworx.pomodoro.ui.fragment.add_task.AddTaskFragment.ARG_PROJECT_MODEL;
import static com.glebworx.pomodoro.ui.fragment.add_task.AddTaskFragment.ARG_TASK_MODEL;

public class AddTaskFragmentPresenter implements IAddTaskFragmentPresenter {


    //                                                                                    ATTRIBUTES

    private IAddTaskFragment presenterListener;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private TaskModel oldTaskModel;
    private boolean isEditing;
    private static SparseArray<String> recurrenceMap;


    //                                                                                  CONSTRUCTORS

    AddTaskFragmentPresenter(@NonNull IAddTaskFragment presenterListener,
                             @Nullable Bundle arguments) {
        this.presenterListener = presenterListener;
        initRecurrenceMap();
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
            taskModel.setPomodorosAllocated(1);
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
    public void selectDueDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        taskModel.setDueDate(calendar.getTime());
        presenterListener.onSelectDueDate(DateTimeManager.getDateString(taskModel.getDueDate(), new Date()));
    }

    @Override
    public void selectPomodorosAllocated(int position) {
        taskModel.setPomodorosAllocated(position + 1);
    }

    @Override
    public void selectRecurrence(int position) {
        taskModel.setRecurrence(recurrenceMap.get(position));
    }

    @Override
    public void addTask() {
        if (taskModel.isValid()) {
            presenterListener.onAddTaskStart();
            if (isEditing) {
                projectModel.setTask(oldTaskModel, taskModel);
            } else {
                projectModel.addTask(taskModel);
            }
            TaskApi.addTask(projectModel, taskModel, task -> {
                if (task.isSuccessful()) {
                    presenterListener.onAddTaskSuccess(isEditing);
                } else {
                    presenterListener.onAddTaskFailure(isEditing);
                }
            });
        } else {
            presenterListener.onTaskValidationFailed(
                    taskModel.getName() == null
                            || taskModel.getName().isEmpty());
        }
    }

    //                                                                                       HELPERS

    private void initRecurrenceMap() {
        recurrenceMap = new SparseArray<>();
        recurrenceMap.put(0, null);
        recurrenceMap.put(1, RECURRENCE_EVERY_DAY);
        recurrenceMap.put(2, RECURRENCE_EVERY_TWO_DAYS);
        recurrenceMap.put(3, RECURRENCE_EVERY_THREE_DAYS);
        recurrenceMap.put(4, RECURRENCE_EVERY_FOUR_DAYS);
        recurrenceMap.put(5, RECURRENCE_EVERY_FIVE_DAYS);
        recurrenceMap.put(6, RECURRENCE_EVERY_SIX_DAYS);
        recurrenceMap.put(7, RECURRENCE_EVERY_WEEKLY);
        recurrenceMap.put(8, RECURRENCE_WEEKDAY);
        recurrenceMap.put(9, RECURRENCE_WEEKEND);
        recurrenceMap.put(10, RECURRENCE_MONTHLY);
    }

}

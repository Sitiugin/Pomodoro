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
import java.util.HashMap;
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


    //                                                                                     CONSTANTS

    private static final SparseArray<String> RECURRENCE_TO_CODE_MAP;
    private static final HashMap<String, Integer> CODE_TO_RECURRENCE_MAP;

    static {
        RECURRENCE_TO_CODE_MAP = new SparseArray<>();
        RECURRENCE_TO_CODE_MAP.put(0, null);
        RECURRENCE_TO_CODE_MAP.put(1, RECURRENCE_EVERY_DAY);
        RECURRENCE_TO_CODE_MAP.put(2, RECURRENCE_EVERY_TWO_DAYS);
        RECURRENCE_TO_CODE_MAP.put(3, RECURRENCE_EVERY_THREE_DAYS);
        RECURRENCE_TO_CODE_MAP.put(4, RECURRENCE_EVERY_FOUR_DAYS);
        RECURRENCE_TO_CODE_MAP.put(5, RECURRENCE_EVERY_FIVE_DAYS);
        RECURRENCE_TO_CODE_MAP.put(6, RECURRENCE_EVERY_SIX_DAYS);
        RECURRENCE_TO_CODE_MAP.put(7, RECURRENCE_EVERY_WEEKLY);
        RECURRENCE_TO_CODE_MAP.put(8, RECURRENCE_WEEKDAY);
        RECURRENCE_TO_CODE_MAP.put(9, RECURRENCE_WEEKEND);
        RECURRENCE_TO_CODE_MAP.put(10, RECURRENCE_MONTHLY);
    }

    static {
        CODE_TO_RECURRENCE_MAP = new HashMap<>();
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_EVERY_DAY, 1);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_EVERY_TWO_DAYS, 2);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_EVERY_THREE_DAYS, 3);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_EVERY_FOUR_DAYS, 4);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_EVERY_FIVE_DAYS, 5);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_EVERY_SIX_DAYS, 6);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_EVERY_WEEKLY, 7);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_WEEKDAY, 8);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_WEEKEND, 9);
        CODE_TO_RECURRENCE_MAP.put(RECURRENCE_MONTHLY, 10);
    }


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
            taskModel.setPomodorosAllocated(1);
        } else {
            isEditing = true;
            oldTaskModel = new TaskModel(taskModel);
        }

        int recurrenceCode;
        if (taskModel.getRecurrence() == null) {
            recurrenceCode = 0;
        } else {
            recurrenceCode = CODE_TO_RECURRENCE_MAP.get(taskModel.getRecurrence());
        }

        presenterListener.onInitView(
                isEditing,
                taskModel.getName(),
                DateTimeManager.getDateString(taskModel.getDueDate(), new Date()),
                taskModel.getPomodorosAllocated(),
                recurrenceCode);

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
        taskModel.setRecurrence(RECURRENCE_TO_CODE_MAP.get(position));
    }

    @Override
    public void saveTask() {

        if (taskModel.isValid()) {

            presenterListener.onAddTaskStart();
            if (isEditing) {
                projectModel.setTask(oldTaskModel, taskModel);
                updateTask();
            } else {
                projectModel.addTask(taskModel);
                addTask();
            }

        } else {

            presenterListener.onTaskValidationFailed(
                    taskModel.getName() == null
                            || taskModel.getName().isEmpty());

        }

    }


    //                                                                                       HELPERS

    private void addTask() {
        TaskApi.addTask(projectModel, taskModel, task -> {
            if (task.isSuccessful()) {
                presenterListener.onAddTaskSuccess(isEditing);
            } else {
                presenterListener.onAddTaskFailure(isEditing);
            }
        });
    }

    private void updateTask() {
        TaskApi.updateTask(projectModel, taskModel, task -> {
            if (task.isSuccessful()) {
                presenterListener.onAddTaskSuccess(isEditing);
            } else {
                presenterListener.onAddTaskFailure(isEditing);
            }
        });
    }

}

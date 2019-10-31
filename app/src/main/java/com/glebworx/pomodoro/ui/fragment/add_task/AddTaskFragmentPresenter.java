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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    private static final Map<Integer, String> RECURRENCE_TO_CODE_MAP;
    private static final Map<String, Integer> CODE_TO_RECURRENCE_MAP;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, null);
        map.put(1, RECURRENCE_EVERY_DAY);
        map.put(2, RECURRENCE_EVERY_TWO_DAYS);
        map.put(3, RECURRENCE_EVERY_THREE_DAYS);
        map.put(4, RECURRENCE_EVERY_FOUR_DAYS);
        map.put(5, RECURRENCE_EVERY_FIVE_DAYS);
        map.put(6, RECURRENCE_EVERY_SIX_DAYS);
        map.put(7, RECURRENCE_EVERY_WEEKLY);
        map.put(8, RECURRENCE_WEEKDAY);
        map.put(9, RECURRENCE_WEEKEND);
        map.put(10, RECURRENCE_MONTHLY);
        RECURRENCE_TO_CODE_MAP = Collections.unmodifiableMap(map);
    }

    static {
        Map<String, Integer> map = new HashMap<>();
        map.put(RECURRENCE_EVERY_DAY, 1);
        map.put(RECURRENCE_EVERY_TWO_DAYS, 2);
        map.put(RECURRENCE_EVERY_THREE_DAYS, 3);
        map.put(RECURRENCE_EVERY_FOUR_DAYS, 4);
        map.put(RECURRENCE_EVERY_FIVE_DAYS, 5);
        map.put(RECURRENCE_EVERY_SIX_DAYS, 6);
        map.put(RECURRENCE_EVERY_WEEKLY, 7);
        map.put(RECURRENCE_WEEKDAY, 8);
        map.put(RECURRENCE_WEEKEND, 9);
        map.put(RECURRENCE_MONTHLY, 10);
        CODE_TO_RECURRENCE_MAP = Collections.unmodifiableMap(map);
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

package com.glebworx.pomodoro.ui.fragment.add_task.interfaces;

import java.util.Date;

public interface IAddTaskFragment {

    void onInitView(boolean isEditing,
                    String taskName,
                    String dueDate,
                    int pomodorosAllocated);

    void onTaskNameChanged();

    void onEditDueDate(Date dueDate);

    void onEditPomodorosAllocated(int pomodorosAllocated);

    void onPomodorosChanged(int pomodorosAllocated);

    void onSelectDueDate(String dateString);

    void onAddTaskStart();

    void onAddTaskSuccess(boolean isEditing);

    void onAddTaskFailure(boolean isEditing);

    void onTaskValidationFailed(boolean isEmpty);

}

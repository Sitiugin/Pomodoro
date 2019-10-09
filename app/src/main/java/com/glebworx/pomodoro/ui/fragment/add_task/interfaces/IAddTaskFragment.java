package com.glebworx.pomodoro.ui.fragment.add_task.interfaces;

public interface IAddTaskFragment {

    void onInitView(boolean isEditing,
                    String taskName,
                    String dueDate,
                    int pomodorosAllocated,
                    String recurrence);

}

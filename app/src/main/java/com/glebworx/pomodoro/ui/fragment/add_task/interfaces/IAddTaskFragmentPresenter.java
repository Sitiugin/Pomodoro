package com.glebworx.pomodoro.ui.fragment.add_task.interfaces;

import android.os.Bundle;

public interface IAddTaskFragmentPresenter {

    void init(Bundle arguments);

    void editTaskName(String name);

    void editDueDate();

    void editPomodorosAllocated();

    void selectDueDate(int year, int monthOfYear, int dayOfMonth);

    void selectPomodorosAllocated(int pomodorosAllocated);

    void saveTask();

}

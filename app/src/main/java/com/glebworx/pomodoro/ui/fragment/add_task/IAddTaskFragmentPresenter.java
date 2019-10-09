package com.glebworx.pomodoro.ui.fragment.add_task;

import android.os.Bundle;

public interface IAddTaskFragmentPresenter {

    void init(Bundle arguments);
    void editTaskName(String name);
    void editDueDate();
    void selectDueDate(int year, int monthOfYear, int dayOfMonth);
    void selectPomodorosAllocated();
    void selectRecurrence();
    void addTask();

}
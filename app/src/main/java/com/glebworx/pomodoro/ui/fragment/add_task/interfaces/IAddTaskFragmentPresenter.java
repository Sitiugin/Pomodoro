package com.glebworx.pomodoro.ui.fragment.add_task.interfaces;

import android.os.Bundle;

public interface IAddTaskFragmentPresenter {

    void init(Bundle arguments);
    void editTaskName(String name);
    void editDueDate();
    void selectDueDate(int year, int monthOfYear, int dayOfMonth);
    void selectPomodorosAllocated(int position);
    void selectRecurrence(int position);
    void addTask();

}

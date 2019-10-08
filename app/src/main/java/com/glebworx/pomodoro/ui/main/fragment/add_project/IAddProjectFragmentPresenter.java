package com.glebworx.pomodoro.ui.main.fragment.add_project;

import android.os.Bundle;

import java.util.Date;

public interface IAddProjectFragmentPresenter {
    void init(Bundle arguments);
    void updateProjectName(String name);
    void updateColorTag(int checkedId);
    void updateDueDate();
    void changeDueDate(int year, int monthOfYear, int dayOfMonth);
    void saveProject();
}

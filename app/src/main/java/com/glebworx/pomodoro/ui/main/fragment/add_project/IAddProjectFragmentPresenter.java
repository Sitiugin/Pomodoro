package com.glebworx.pomodoro.ui.main.fragment.add_project;

import android.os.Bundle;

import java.util.Date;

public interface IAddProjectFragmentPresenter {

    void init(Bundle arguments);
    void editProjectName(String name);
    void selectColorTag(int checkedId);
    void editDueDate();
    void selectDueDate(int year, int monthOfYear, int dayOfMonth);
    void saveProject();

}

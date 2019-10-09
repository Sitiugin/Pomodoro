package com.glebworx.pomodoro.ui.fragment.add_project.interfaces;

import android.os.Bundle;

public interface IAddProjectFragmentPresenter {

    void init(Bundle arguments);
    void editProjectName(String name);
    void selectColorTag(int checkedId);
    void editDueDate();
    void selectDueDate(int year, int monthOfYear, int dayOfMonth);
    void saveProject();

}

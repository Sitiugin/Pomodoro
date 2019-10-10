package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

public interface IProjectsFragmentPresenter {

    void init();
    //void onQueryChanged();
    void onProjectClicked();
    void onProjectEditSwiped();
    void onProjectDeleteSwiped();
    void onAddProjectClicked();
}

package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;

public interface IProjectsFragmentPresenter {

    void init();

    void deleteProject(ProjectItem projectItem, int position);
}

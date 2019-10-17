package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;

public interface IProjectsFragmentPresenter {

    void init();

    void destroy();

    void viewProject(ProjectItem projectItem);

    void editProject(ProjectItem projectItem);

    void deleteProject(ProjectItem projectItem, int position);

}

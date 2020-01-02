package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import android.content.Context;

import com.glebworx.pomodoro.ui.item.ProjectItem;

public interface IProjectsFragmentPresenter {

    void init();

    void destroy();

    //void refreshTasksHeader();

    void viewProject(ProjectItem projectItem);

    void editProject(ProjectItem projectItem);

    void deleteProject(ProjectItem projectItem, int position);

    void sendFeedback(Context context);

}

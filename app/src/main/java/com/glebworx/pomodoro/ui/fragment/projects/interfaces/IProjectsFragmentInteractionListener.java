package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.model.ProjectModel;

public interface IProjectsFragmentInteractionListener {

    void onViewReport();

    void onAddProject();

    void onEditProject(ProjectModel projectModel);

    void onViewProject(ProjectModel projectModel);

    void onViewTodayTasks();

    void onViewThisWeekTasks();

    void onViewOverdueTasks();

    void onViewAboutInfo();

    void onViewProjectArchive();

}

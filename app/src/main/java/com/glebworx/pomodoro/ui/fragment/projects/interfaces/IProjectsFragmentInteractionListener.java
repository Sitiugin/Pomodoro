package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.model.ProjectModel;

public interface IProjectsFragmentInteractionListener {

    void onAddProject();
    void onViewProject(ProjectModel projectModel);
    void onViewReport();
    void onEditProject(ProjectModel projectModel);

}

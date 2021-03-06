package com.glebworx.pomodoro.ui.fragment.view_project.interfaces;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

public interface IViewProjectFragmentInteractionListener {

    void onEditProject(ProjectModel projectModel);

    void onAddTask(ProjectModel projectModel);

    void onSelectTask(ProjectModel projectModel, TaskModel taskModel);

    void onEditTask(ProjectModel projectModel, TaskModel taskModel);

    void onViewProjectReport(ProjectModel projectModel);

    void onCloseFragment();

}

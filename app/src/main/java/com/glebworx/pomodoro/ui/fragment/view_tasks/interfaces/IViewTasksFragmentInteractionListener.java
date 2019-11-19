package com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

public interface IViewTasksFragmentInteractionListener {

    void onSelectTask(ProjectModel projectModel, TaskModel taskModel);

    void onViewProject(ProjectModel projectModel);

    void onCloseFragment();

}

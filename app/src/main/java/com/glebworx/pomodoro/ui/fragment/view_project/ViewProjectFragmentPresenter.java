package com.glebworx.pomodoro.ui.fragment.view_project;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragment;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;

import java.util.Date;

import static com.glebworx.pomodoro.ui.fragment.view_project.ViewProjectFragment.ARG_PROJECT_MODEL;

public class ViewProjectFragmentPresenter implements IViewProjectFragmentPresenter {

    private IViewProjectFragment presenterListener;
    private IViewProjectFragmentInteractionListener interactionListener;
    private ProjectModel projectModel;

    public ViewProjectFragmentPresenter(@NonNull IViewProjectFragment presenterListener,
                                        @Nullable IViewProjectFragmentInteractionListener interactionListener,
                                        @Nullable Bundle arguments,
                                        View.OnClickListener headerClickListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(arguments, headerClickListener);
    }

    @Override
    public void init(Bundle arguments, View.OnClickListener onClickListener) {
        projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        if (projectModel == null) {
            projectModel = new ProjectModel();
        }
        presenterListener.onInitView(projectModel.getName(), new ViewProjectHeaderItem(projectModel, onClickListener));
    }

    @Override
    public void editProject() {
        if (interactionListener != null) {
            interactionListener.onEditProject(projectModel);
        }
    }

    @Override
    public void deleteProject() {
        ProjectApi.deleteProject(
                projectModel,
                task -> presenterListener.onProjectDeleted(task.isSuccessful()));
    }

    @Override
    public void addTask() {
        if (interactionListener != null) {
            interactionListener.onAddTask(projectModel);
        }
    }

    @Override
    public void editTask(TaskItem taskItem) {
        if (interactionListener != null) {
            interactionListener.onEditTask(projectModel, taskItem.getModel());
        }
    }

    @Override
    public void deleteTask(TaskItem taskItem, int position) {
        TaskApi.deleteTask(
                projectModel,
                taskItem.getModel(),
                task -> presenterListener.onTaskDeleted(task.isSuccessful(), position));
    }

    @Override
    public void selectTask(TaskItem taskItem) {
        if (interactionListener != null) {
            interactionListener.onSelectTask(projectModel, taskItem.getModel());
        }
    }

    @Override
    public void updateHeaderItem() {
        presenterListener.onHeaderItemChanged(
                projectModel.getEstimatedTime(),
                projectModel.getElapsedTime(),
                projectModel.getProgressRatio());
    }

    @Override
    public void updateSubtitle() {
        presenterListener.onSubtitleChanged(projectModel.getDueDate(), new Date());
    }

}

package com.glebworx.pomodoro.ui.fragment.projects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

public class ProjectsFragmentPresenter implements IProjectsFragmentPresenter {

    private @NonNull IProjectsFragment presenterListener;
    private @Nullable
    IProjectsFragmentInteractionListener interactionListener;

    public ProjectsFragmentPresenter(@NonNull IProjectsFragment presenterListener,
                                     @Nullable IProjectsFragmentInteractionListener interactionListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init();
    }

    @Override
    public void init() {
        IItemAdapter.Predicate<ProjectItem> predicate = getFilterPredicate();
        presenterListener.onInitView(predicate);
    }

    @Override
    public void viewProject(ProjectItem projectItem) {
        if (interactionListener != null) {
            interactionListener.onViewProject(projectItem.getModel());
        }
    }

    @Override
    public void editProject(ProjectItem projectItem) {
        if (interactionListener != null) {
            interactionListener.onEditProject(projectItem.getModel());
        }
    }

    @Override
    public void deleteProject(ProjectItem projectItem, int position) {
        ProjectApi.deleteProject(projectItem.getModel(), task -> {
            if (!task.isSuccessful()) {
                presenterListener.onDeleteProjectFailed(position);
            }
        });
    }

    private IItemAdapter.Predicate<ProjectItem> getFilterPredicate() {
        return (item, constraint) -> {
            if (constraint == null) {
                return true;
            }
            String title = item.getModel().getName();
            return title != null && title.toLowerCase().contains(constraint);
        };
    }

}

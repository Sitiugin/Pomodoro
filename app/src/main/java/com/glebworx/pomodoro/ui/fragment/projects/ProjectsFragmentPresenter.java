package com.glebworx.pomodoro.ui.fragment.projects;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

public class ProjectsFragmentPresenter implements IProjectsFragmentPresenter {

    private @NonNull IProjectsFragment presenterListener;

    public ProjectsFragmentPresenter(@NonNull IProjectsFragment presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        IItemAdapter.Predicate<ProjectItem> predicate = getFilterPredicate();
        presenterListener.onInitView(predicate);
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

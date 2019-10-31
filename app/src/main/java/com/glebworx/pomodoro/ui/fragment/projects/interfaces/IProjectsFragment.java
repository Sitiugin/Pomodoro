package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

public interface IProjectsFragment {

    void onInitView(IItemAdapter.Predicate<ProjectItem> predicate);

    void onItemAdded(ProjectItem item);

    void onItemModified(ProjectItem item);

    void onItemDeleted(ProjectItem item);

    void onDeleteProjectFailed(int position);

}

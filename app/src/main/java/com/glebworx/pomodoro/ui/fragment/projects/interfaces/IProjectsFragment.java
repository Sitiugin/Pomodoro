package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

public interface IProjectsFragment {

    void onInitView(IItemAdapter.Predicate<ProjectItem> predicate);

    void onDeleteProjectFailed(int position);

}

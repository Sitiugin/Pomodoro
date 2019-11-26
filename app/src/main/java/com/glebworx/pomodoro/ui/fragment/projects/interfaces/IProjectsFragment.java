package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.ui.item.ProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

public interface IProjectsFragment {

    void onInitView(IItemAdapter.Predicate<ProjectItem> predicate);

    void onItemAdded(ProjectItem item);

    void onItemModified(ProjectItem item);

    void onItemDeleted(ProjectItem item);

    void onTodayTaskCountChanged(int todayTaskCount);

    void onThisWeekTaskCountChanged(int thisWeekTaskCount);

    void onOverdueTaskCountChanged(int overdueTaskCount);

    void onDeleteProjectFailed(int position);

}

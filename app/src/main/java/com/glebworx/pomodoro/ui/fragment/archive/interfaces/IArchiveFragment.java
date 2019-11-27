package com.glebworx.pomodoro.ui.fragment.archive.interfaces;

import com.glebworx.pomodoro.ui.item.ProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

public interface IArchiveFragment {

    void onInitView(IItemAdapter.Predicate<ProjectItem> predicate);

    void onItemAdded(ProjectItem item);

    void onItemModified(ProjectItem item);

    void onItemDeleted(ProjectItem item);

    void onDeleteProjectFailed(int position);

}

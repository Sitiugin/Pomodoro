package com.glebworx.pomodoro.ui.fragment.archive.interfaces;

import com.glebworx.pomodoro.ui.item.ProjectItem;

public interface IArchiveFragmentPresenter {

    void init();

    void destroy();

    void restoreProject(ProjectItem projectItem);

    void deleteProject(ProjectItem projectItem, int position);

}

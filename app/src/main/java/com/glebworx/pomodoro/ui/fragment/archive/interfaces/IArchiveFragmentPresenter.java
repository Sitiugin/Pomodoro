package com.glebworx.pomodoro.ui.fragment.archive.interfaces;

import com.glebworx.pomodoro.ui.fragment.archive.item.ArchivedProjectItem;

public interface IArchiveFragmentPresenter {

    void init();

    void destroy();

    void restoreProject(ArchivedProjectItem projectItem);

    void deleteProject(ArchivedProjectItem projectItem, int position);

    void deleteAll();

}

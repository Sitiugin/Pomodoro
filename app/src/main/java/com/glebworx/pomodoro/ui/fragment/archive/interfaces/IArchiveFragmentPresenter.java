package com.glebworx.pomodoro.ui.fragment.archive.interfaces;

import com.glebworx.pomodoro.ui.fragment.archive.item.ArchivedProjectItem;

public interface IArchiveFragmentPresenter {

    void init();

    void destroy();

    void deleteProject(ArchivedProjectItem projectItem, int position);

    void restoreProject(ArchivedProjectItem projectItem, int position);

    void deleteAll();

}

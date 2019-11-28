package com.glebworx.pomodoro.ui.fragment.archive.interfaces;

import com.glebworx.pomodoro.ui.fragment.archive.item.ArchivedProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

public interface IArchiveFragment {

    void onInitView(IItemAdapter.Predicate<ArchivedProjectItem> predicate);

    void onItemAdded(ArchivedProjectItem item);

    void onItemModified(ArchivedProjectItem item);

    void onItemDeleted(ArchivedProjectItem item);

    void onDeleteProjectFailed(int position);

    void onDeleteAllFinished(boolean isSuccessful);

}

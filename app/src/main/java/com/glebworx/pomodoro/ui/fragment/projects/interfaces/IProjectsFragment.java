package com.glebworx.pomodoro.ui.fragment.projects.interfaces;

import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.google.firebase.firestore.DocumentChange;
import com.mikepenz.fastadapter.IItemAdapter;

import io.reactivex.Observable;

public interface IProjectsFragment {

    void onInitView(IItemAdapter.Predicate<ProjectItem> predicate, Observable<DocumentChange> observable);

    void onDeleteProjectFailed(int position);

}

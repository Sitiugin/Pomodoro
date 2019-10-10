package com.glebworx.pomodoro.ui.fragment.projects;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentPresenter;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

public class ProjectsFragmentPresenter implements IProjectsFragmentPresenter {

    private @NonNull IProjectsFragment presenterListener;
    private EventListener<QuerySnapshot> taskCountEventListener;
    private EventListener<QuerySnapshot> projectsEventListener;

    public ProjectsFragmentPresenter(@NonNull IProjectsFragment presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        presenterListener.onInitView();
    }

    @Override
    public void onProjectClicked() {

    }

    @Override
    public void onProjectEditSwiped() {

    }

    @Override
    public void onProjectDeleteSwiped() {

    }

    @Override
    public void onAddProjectClicked() {

    }

}

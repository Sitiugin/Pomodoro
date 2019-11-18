package com.glebworx.pomodoro.ui.fragment.view_tasks;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragment;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentPresenter;

import javax.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;

public class ViewTasksFragmentPresenter implements IViewTasksFragmentPresenter {

    private @NonNull
    IViewTasksFragment presenterListener;
    private @NonNull
    IViewTasksFragmentInteractionListener interactionListener;
    private @NonNull
    String type;
    private CompositeDisposable compositeDisposable;

    public ViewTasksFragmentPresenter(@NonNull IViewTasksFragment presenterListener,
                                      @NonNull IViewTasksFragmentInteractionListener interactionListener,
                                      @Nullable Bundle arguments) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(arguments);
    }

    @Override
    public void init(Bundle arguments) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void selectTask(TaskItem taskItem) {

    }
}

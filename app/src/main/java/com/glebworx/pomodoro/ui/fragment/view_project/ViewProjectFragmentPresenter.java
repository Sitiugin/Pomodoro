package com.glebworx.pomodoro.ui.fragment.view_project;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragment;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.ui.fragment.view_project.ViewProjectFragment.ARG_PROJECT_MODEL;

public class ViewProjectFragmentPresenter implements IViewProjectFragmentPresenter {

    private @NonNull
    IViewProjectFragment presenterListener;
    private @Nullable
    IViewProjectFragmentInteractionListener interactionListener;
    private @NonNull
    ProjectModel projectModel;
    private @NonNull
    Observable<DocumentChange> observable;

    public ViewProjectFragmentPresenter(@NonNull IViewProjectFragment presenterListener,
                                        @Nullable IViewProjectFragmentInteractionListener interactionListener,
                                        @Nullable Bundle arguments,
                                        View.OnClickListener headerClickListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(arguments, headerClickListener);
    }

    @Override
    public void init(Bundle arguments, View.OnClickListener onClickListener) {
        projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        if (projectModel == null) {
            projectModel = new ProjectModel();
        }
        observable = getTaskEventObservable(projectModel.getName());
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        presenterListener.onInitView(
                projectModel.getName(),
                new ViewProjectHeaderItem(projectModel, onClickListener),
                observable);
    }

    @Override
    public void destroy() {
        observable.unsubscribeOn(Schedulers.io());
    }

    @Override
    public void editProject() {
        if (interactionListener != null) {
            interactionListener.onEditProject(projectModel);
        }
    }

    @Override
    public void deleteProject() {
        ProjectApi.deleteProject(
                projectModel,
                task -> presenterListener.onProjectDeleted(task.isSuccessful()));
    }

    @Override
    public void addTask() {
        if (interactionListener != null) {
            interactionListener.onAddTask(projectModel);
        }
    }

    @Override
    public void editTask(TaskItem taskItem) {
        if (interactionListener != null) {
            interactionListener.onEditTask(projectModel, taskItem.getModel());
        }
    }

    @Override
    public void deleteTask(TaskItem taskItem, int position) {
        TaskApi.deleteTask(
                projectModel,
                taskItem.getModel(),
                task -> presenterListener.onTaskDeleted(task.isSuccessful(), position));
    }

    @Override
    public void selectTask(TaskItem taskItem) {
        if (interactionListener != null) {
            interactionListener.onSelectTask(projectModel, taskItem.getModel());
        }
    }

    @Override
    public void updateHeaderItem() {
        presenterListener.onHeaderItemChanged(
                projectModel.getEstimatedTime(),
                projectModel.getElapsedTime(),
                projectModel.getProgressRatio());
    }

    @Override
    public void updateSubtitle() {
        presenterListener.onSubtitleChanged(projectModel.getDueDate(), new Date());
    }

    private Observable<DocumentChange> getTaskEventObservable(@NonNull String projectName) {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addTaskEventListener(projectName, (querySnapshot, e) -> {
                if (e != null) {
                    emitter.onError(e);
                    return;
                }
                if (querySnapshot == null || querySnapshot.isEmpty()) {
                    return;
                }
                List<DocumentChange> documentChanges = querySnapshot.getDocumentChanges();
                for (DocumentChange change : documentChanges) {
                    emitter.onNext(change);
                }
            });
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

}

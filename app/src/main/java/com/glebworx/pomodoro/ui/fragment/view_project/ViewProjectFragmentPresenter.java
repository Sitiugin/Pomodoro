package com.glebworx.pomodoro.ui.fragment.view_project;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragment;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.ui.fragment.view_project.ViewProjectFragment.ARG_PROJECT_MODEL;


public class ViewProjectFragmentPresenter implements IViewProjectFragmentPresenter {

    private @NonNull
    IViewProjectFragment presenterListener;
    private @NonNull
    IViewProjectFragmentInteractionListener interactionListener;
    private @NonNull
    ProjectModel projectModel;

    private CompositeDisposable compositeDisposable;

    ViewProjectFragmentPresenter(@NonNull IViewProjectFragment presenterListener,
                                 @NonNull IViewProjectFragmentInteractionListener interactionListener,
                                 @Nullable Bundle arguments,
                                 View.OnClickListener headerClickListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(Objects.requireNonNull(arguments), headerClickListener);
    }

    @Override
    public void init(Bundle arguments, View.OnClickListener onClickListener) {

        projectModel = Objects.requireNonNull(arguments.getParcelable(ARG_PROJECT_MODEL));

        String projectName = projectModel.getName();

        compositeDisposable = new CompositeDisposable();

        Observable<DocumentChange> observable = getObservable(projectName, false);
        observable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        Observable<DocumentChange> completedObservable = getObservable(projectName, true);
        completedObservable = completedObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        Observable<DocumentSnapshot> headerObservable = getHeaderObservable(projectName);
        headerObservable = headerObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        presenterListener.onInitView(
                projectModel.getName(),
                new ViewProjectHeaderItem(projectModel, onClickListener));

        observable.subscribe(getObserver());
        completedObservable.subscribe(getCompletedObserver());
        headerObservable.subscribe(getHeaderObserver());

    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    @Override
    public void editProject() {
        interactionListener.onEditProject(projectModel);
    }

    @Override
    public void deleteProject() {
        ProjectApi.deleteProject(
                projectModel,
                task -> presenterListener.onProjectDeleted(task.isSuccessful()));
    }

    @Override
    public void addTask() {
        interactionListener.onAddTask(projectModel);
    }

    @Override
    public void editTask(TaskItem taskItem) {
        interactionListener.onEditTask(projectModel, taskItem.getModel());
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
        interactionListener.onSelectTask(projectModel, taskItem.getModel());
    }

    private EventListener<QuerySnapshot> getEventListener(ObservableEmitter<DocumentChange> emitter) {
        return (querySnapshot, e) -> {
            if (emitter.isDisposed()) {
                return;
            }
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
        };
    }

    private Observable<DocumentChange> getObservable(@NonNull String projectName, boolean completed) {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addTaskEventListener(projectName, getEventListener(emitter), completed);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private Observable<DocumentSnapshot> getHeaderObservable(@NonNull String projectName) {
        return Observable.create(emitter -> {
            ListenerRegistration taskEventListenerRegistration = ProjectApi.addProjectEventListener(
                    projectName,
                    (documentSnapshot, e) -> {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        if (e != null) {
                            emitter.onError(e);
                            return;
                        }
                        if (documentSnapshot == null) {
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    });
            emitter.setCancellable(taskEventListenerRegistration::remove);
        });
    }

    private io.reactivex.Observer<DocumentChange> getObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentChange documentChange) {
                TaskItem item = new TaskItem(documentChange.getDocument().toObject(TaskModel.class));
                switch (documentChange.getType()) {
                    case ADDED:
                        presenterListener.onTaskAdded(item);
                        break;
                    case MODIFIED:
                        presenterListener.onTaskModified(item);
                        break;
                    case REMOVED:
                        presenterListener.onTaskDeleted(item);
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<DocumentChange> getCompletedObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentChange documentChange) {
                TaskModel model = documentChange.getDocument().toObject(TaskModel.class);
                CompletedTaskItem completedItem = new CompletedTaskItem(model);
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    presenterListener.onTaskCompleted(completedItem);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<DocumentSnapshot> getHeaderObserver() {
        return new io.reactivex.Observer<DocumentSnapshot>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentSnapshot documentSnapshot) {
                ProjectModel projectModel = documentSnapshot.toObject(ProjectModel.class);
                if (projectModel == null) {
                    return;
                }
                presenterListener.onHeaderItemChanged(
                        projectModel.getColorTag(),
                        projectModel.getEstimatedTime(),
                        projectModel.getElapsedTime(),
                        projectModel.getProgress());
                presenterListener.onSubtitleChanged(projectModel.getDueDate(), new Date());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

}

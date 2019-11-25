package com.glebworx.pomodoro.ui.fragment.view_tasks;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragment;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentPresenter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.ui.fragment.view_tasks.ViewTasksFragment.ARG_TYPE;
import static com.glebworx.pomodoro.ui.fragment.view_tasks.ViewTasksFragment.TYPE_OVERDUE;
import static com.glebworx.pomodoro.ui.fragment.view_tasks.ViewTasksFragment.TYPE_THIS_WEEK;
import static com.glebworx.pomodoro.ui.fragment.view_tasks.ViewTasksFragment.TYPE_TODAY;

public class ViewTasksFragmentPresenter implements IViewTasksFragmentPresenter {

    private @NonNull
    IViewTasksFragment presenterListener;
    private @NonNull
    IViewTasksFragmentInteractionListener interactionListener;
    private @NonNull
    String type;
    private @NonNull
    Map<String, ProjectModel> projectModelMap; // TODO put models into map when added
    private CompositeDisposable compositeDisposable;

    ViewTasksFragmentPresenter(@NonNull IViewTasksFragment presenterListener,
                               @NonNull IViewTasksFragmentInteractionListener interactionListener,
                               @Nullable Bundle arguments) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(Objects.requireNonNull(arguments));
    }

    @Override
    public void init(Bundle arguments) {

        compositeDisposable = new CompositeDisposable();

        type = Objects.requireNonNull(arguments.getString(ARG_TYPE));
        projectModelMap = new HashMap<>();

        Observable<DocumentChange> observable = null;
        Observable<DocumentChange> completedObservable = null;
        switch (type) {
            case TYPE_TODAY:
                observable = getTodayObservable(false);
                completedObservable = getTodayObservable(true);
                break;
            case TYPE_THIS_WEEK:
                observable = getThisWeekObservable(false);
                completedObservable = getThisWeekObservable(true);
                break;
            case TYPE_OVERDUE:
                observable = getOverdueObservable(false);
                completedObservable = getOverdueObservable(true);
                break;
        }
        if (observable != null) {
            observable = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());
            observable.subscribe(getObserver());
        }
        if (completedObservable != null) {
            completedObservable = completedObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());
            completedObservable.subscribe(getCompletedObserver());
        }

        presenterListener.onInitView(type);

    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    @Override
    public void selectTask(TaskItem taskItem) {
        ProjectModel projectModel = projectModelMap.get(taskItem.getProjectName());
        if (projectModel != null) {
            interactionListener.onSelectTask(projectModel, taskItem.getModel());
        }
    }

    @Override
    public void viewProject(String projectName) {
        ProjectModel projectModel = projectModelMap.get(projectName);
        if (projectModel != null) {
            interactionListener.onViewProject(projectModel);
        }
    }

    private Observable<DocumentChange> getTodayObservable(boolean completed) {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addTodayTasksEventListener(getObservableEventListener(emitter), completed);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private Observable<DocumentChange> getThisWeekObservable(boolean completed) {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addThisWeekTasksEventListener(getObservableEventListener(emitter), completed);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private Observable<DocumentChange> getOverdueObservable(boolean completed) {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addOverdueTasksEventListener(getObservableEventListener(emitter), completed);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private EventListener<QuerySnapshot> getObservableEventListener(ObservableEmitter<DocumentChange> emitter) {
        return (querySnapshot, e) -> {
            if (emitter.isDisposed()) {
                return;
            }
            if (e != null) {
                emitter.onError(e);
                return;
            }
            if (querySnapshot == null) {
                return;
            }
            List<DocumentChange> documentChanges = querySnapshot.getDocumentChanges();
            for (DocumentChange change : documentChanges) {
                emitter.onNext(change);
            }
        };
    }

    private Observable<DocumentSnapshot> getProjectObservable(@NonNull String projectName) {
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
                        observeProject(item.getProjectName());
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
                    observeProject(completedItem.getProjectName());
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

    private void observeProject(String projectName) {
        Observable<DocumentSnapshot> projectObservable = getProjectObservable(projectName);
        projectObservable = projectObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
        projectObservable.subscribe(getProjectObserver());
    }

    private io.reactivex.Observer<DocumentSnapshot> getProjectObserver() {
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
                projectModelMap.put(projectModel.getName(), projectModel);
                presenterListener.onProjectChanged(projectModel.getName(), projectModel.getColorTag());
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

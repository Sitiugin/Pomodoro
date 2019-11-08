package com.glebworx.pomodoro.ui.fragment.projects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProjectsFragmentPresenter implements IProjectsFragmentPresenter {

    private @NonNull IProjectsFragment presenterListener;
    private @Nullable
    IProjectsFragmentInteractionListener interactionListener;
    private Observable<DocumentChange> projectsObservable;
    private Observable<Integer> todayTasksObservable;
    private Observable<Integer> thisWeekTasksObservable;
    private Observable<Integer> overdueTasksObservable;
    private CompositeDisposable compositeDisposable;

    public ProjectsFragmentPresenter(@NonNull IProjectsFragment presenterListener,
                                     @Nullable IProjectsFragmentInteractionListener interactionListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init();
    }

    @Override
    public void init() {

        IItemAdapter.Predicate<ProjectItem> predicate = getFilterPredicate();

        compositeDisposable = new CompositeDisposable();

        projectsObservable = getProjectEventObservable();
        projectsObservable = projectsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        presenterListener.onInitView(predicate);

        projectsObservable.subscribe(getProjectEventObserver());

        refreshTasksHeader();

    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    @Override
    public void refreshTasksHeader() {
        TaskApi.getTodayTasks(task -> {
            todayTasksObservable = getObservable(task.getResult());
            todayTasksObservable = todayTasksObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());
            todayTasksObservable.subscribe(getTodayTasksObserver());
        });
        TaskApi.getThisWeekTasks(task -> {
            thisWeekTasksObservable = getObservable(task.getResult());
            thisWeekTasksObservable = thisWeekTasksObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());
            thisWeekTasksObservable.subscribe(getThisWeekTasksObserver());
        });
        TaskApi.getOverdueTasks(task -> {
            overdueTasksObservable = getObservable(task.getResult());
            overdueTasksObservable = overdueTasksObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());
            overdueTasksObservable.subscribe(getOverdueTasksObserver());
        });
    }

    @Override
    public void viewProject(ProjectItem projectItem) {
        if (interactionListener != null) {
            interactionListener.onViewProject(projectItem.getModel());
        }
    }

    @Override
    public void editProject(ProjectItem projectItem) {
        if (interactionListener != null) {
            interactionListener.onEditProject(projectItem.getModel());
        }
    }

    @Override
    public void deleteProject(ProjectItem projectItem, int position) {
        ProjectApi.deleteProject(projectItem.getModel(), task -> {
            if (!task.isSuccessful()) {
                presenterListener.onDeleteProjectFailed(position);
            }
        });
    }

    private IItemAdapter.Predicate<ProjectItem> getFilterPredicate() {
        return (item, constraint) -> {
            if (constraint == null) {
                return true;
            }
            String title = item.getModel().getName();
            return title != null && title.toLowerCase().contains(constraint);
        };
    }

    private Observable<DocumentChange> getProjectEventObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = ProjectApi.addModelEventListener((querySnapshot, e) -> {
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
            });
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private io.reactivex.Observer<DocumentChange> getProjectEventObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocumentChange documentChange) {
                ProjectItem item = new ProjectItem(documentChange.getDocument().toObject(ProjectModel.class));
                switch (documentChange.getType()) {
                    case ADDED:
                        presenterListener.onItemAdded(item);
                        break;
                    case MODIFIED:
                        presenterListener.onItemModified(item);
                        break;
                    case REMOVED:
                        presenterListener.onItemDeleted(item);
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

    private Observable<Integer> getObservable(QuerySnapshot snapshot) {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            emitter.onNext(snapshot.getDocuments().size());
            emitter.onComplete();
        });
    }

    private io.reactivex.Observer<Integer> getTodayTasksObserver() {
        return new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(Integer todayTaskCount) {
                presenterListener.onTodayTaskCountChanged(todayTaskCount);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<Integer> getThisWeekTasksObserver() {
        return new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(Integer thisWeekTaskCount) {
                presenterListener.onThisWeekTaskCountChanged(thisWeekTaskCount);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<Integer> getOverdueTasksObserver() {
        return new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(Integer overdueTaskCount) {
                presenterListener.onOverdueTaskCountChanged(overdueTaskCount);
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

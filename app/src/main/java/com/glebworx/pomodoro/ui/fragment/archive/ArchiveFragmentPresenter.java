package com.glebworx.pomodoro.ui.fragment.archive;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.archive.item.ArchivedProjectItem;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class ArchiveFragmentPresenter implements IArchiveFragmentPresenter {

    private @NonNull
    IArchiveFragment presenterListener;
    private @NonNull
    IArchiveFragmentInteractionListener interactionListener;
    private CompositeDisposable compositeDisposable;

    ArchiveFragmentPresenter(@NonNull IArchiveFragment presenterListener,
                             @NonNull IArchiveFragmentInteractionListener interactionListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init();
    }

    @Override
    public void init() {

        IItemAdapter.Predicate<ArchivedProjectItem> predicate = getFilterPredicate();

        compositeDisposable = new CompositeDisposable();

        Observable<DocumentChange> projectsObservable = getProjectEventObservable();
        projectsObservable = projectsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        presenterListener.onInitView(predicate);

        projectsObservable.subscribe(getProjectEventObserver());

    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    @Override
    public void restoreProject(ArchivedProjectItem projectItem, int position) {
        ProjectApi.restoreProject(projectItem.getModel(), task -> {
            if (!task.isSuccessful()) {
                presenterListener.onRestoreProjectFailed(position);
            }
        });
    }

    @Override
    public void deleteProject(ArchivedProjectItem projectItem, int position) {
        ProjectApi.deleteProject(projectItem.getModel(), task -> {
            if (!task.isSuccessful()) {
                presenterListener.onDeleteProjectFailed(position);
            }
        });
    }

    @Override
    public void deleteProjects(List<ArchivedProjectItem> projectItemList) {
        List<ProjectModel> projectModels = new ArrayList<>();
        for (ArchivedProjectItem item : projectItemList) {
            projectModels.add(item.getModel());
        }
        ProjectApi.deleteProjects(projectModels, task -> presenterListener.onDeleteAllFinished(task.isSuccessful()));
    }

    private IItemAdapter.Predicate<ArchivedProjectItem> getFilterPredicate() {
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
            ListenerRegistration listenerRegistration = ProjectApi.addProjectsEventListener((querySnapshot, e) -> {
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
            }, true);
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
                ArchivedProjectItem item = new ArchivedProjectItem(documentChange.getDocument().toObject(ProjectModel.class));
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
                presenterListener.onUpdateDeleteAllButtonState();
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

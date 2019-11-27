package com.glebworx.pomodoro.ui.fragment.archive;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentPresenter;
import com.glebworx.pomodoro.ui.item.ProjectItem;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.mikepenz.fastadapter.IItemAdapter;

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

        IItemAdapter.Predicate<ProjectItem> predicate = getFilterPredicate();

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

}

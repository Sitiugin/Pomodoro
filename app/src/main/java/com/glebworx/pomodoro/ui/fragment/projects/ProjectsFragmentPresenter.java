package com.glebworx.pomodoro.ui.fragment.projects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentPresenter;
import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProjectsFragmentPresenter implements IProjectsFragmentPresenter {

    private @NonNull IProjectsFragment presenterListener;
    private @Nullable
    IProjectsFragmentInteractionListener interactionListener;
    private @NonNull
    Observable<DocumentChange> observable;

    public ProjectsFragmentPresenter(@NonNull IProjectsFragment presenterListener,
                                     @Nullable IProjectsFragmentInteractionListener interactionListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init();
    }

    @Override
    public void init() {
        IItemAdapter.Predicate<ProjectItem> predicate = getFilterPredicate();
        observable = getProjectkEventObservable();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        presenterListener.onInitView(predicate, observable);
    }

    @Override
    public void destroy() {
        observable.unsubscribeOn(Schedulers.io());
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

    private Observable<DocumentChange> getProjectkEventObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = ProjectApi.addModelEventListener((querySnapshot, e) -> {
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

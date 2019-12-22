package com.glebworx.pomodoro.ui.fragment.projects;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.activity.SplashActivity;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentPresenter;
import com.glebworx.pomodoro.ui.item.ProjectItem;
import com.glebworx.pomodoro.util.TimestampFieldComparator;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProjectsFragmentPresenter implements IProjectsFragmentPresenter {

    private @NonNull IProjectsFragment presenterListener;
    private @Nullable
    IProjectsFragmentInteractionListener interactionListener;
    private CompositeDisposable compositeDisposable;

    ProjectsFragmentPresenter(@NonNull IProjectsFragment presenterListener,
                              @Nullable IProjectsFragmentInteractionListener interactionListener) {
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

        Observable<Integer> todayTasksObservable = getTodayObservable();
        todayTasksObservable = todayTasksObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        Observable<Integer> thisWeekTasksObservable = getThisWeekObservable();
        thisWeekTasksObservable = thisWeekTasksObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        Observable<Integer> overdueTasksObservable = getOverdueObservable();
        overdueTasksObservable = overdueTasksObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        presenterListener.onInitView(predicate);

        projectsObservable.subscribe(getProjectEventObserver());
        todayTasksObservable.subscribe(getTodayTasksObserver());
        thisWeekTasksObservable.subscribe(getThisWeekTasksObserver());
        overdueTasksObservable.subscribe(getOverdueTasksObserver());

    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
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

    @Override
    public void sendFeedback(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse(context.getString(R.string.email_address_mailto)));
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject_feedback));
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);
        if (list.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.email_no_clients_found), Toast.LENGTH_LONG).show();
        } else {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.email_title_send_feedback)));
        }
    }

    @Override
    public void signOut(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        AuthManager.getInstance().signOut();
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
                List<DocumentChange> copy = new ArrayList<>(documentChanges);
                copy.sort(new TimestampFieldComparator("timestamp"));
                for (DocumentChange change : copy) {
                    emitter.onNext(change);
                }
            }, false);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private io.reactivex.Observer<DocumentChange> getProjectEventObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
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

    private Observable<Integer> getTodayObservable() {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            ListenerRegistration listenerRegistration = TaskApi.addTodayTasksNoChangesEventListener(getObservableEventListener(emitter), false);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private Observable<Integer> getThisWeekObservable() {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            ListenerRegistration listenerRegistration = TaskApi.addThisWeekTasksNoChangesEventListener(getObservableEventListener(emitter), false);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private Observable<Integer> getOverdueObservable() {
        return Observable.create(emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            ListenerRegistration listenerRegistration = TaskApi.addOverdueTasksNoChangesEventListener(getObservableEventListener(emitter), false);
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private EventListener<QuerySnapshot> getObservableEventListener(ObservableEmitter<Integer> emitter) {
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
            emitter.onNext(querySnapshot.getDocuments().size());
        };
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

package com.glebworx.pomodoro.ui.fragment.view_tasks;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragment;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentPresenter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
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

        compositeDisposable = new CompositeDisposable();

        type = Objects.requireNonNull(arguments.getString(ARG_TYPE));
        Observable<DocumentChange> observable;
        switch (type) {
            case TYPE_TODAY:
                observable = getTodayObservable();
                observable = initObservable(observable);
                observable.subscribe(getTodayTasksObserver());
                break;
            case TYPE_THIS_WEEK:
                observable = getThisWeekObservable();
                observable = initObservable(observable);
                observable.subscribe(getThisWeekTasksObserver());
                break;
            case TYPE_OVERDUE:
                observable = getOverdueObservable();
                observable = initObservable(observable);
                observable.subscribe(getOverdueTasksObserver());
                break;
        }

        presenterListener.onInitView(type);
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    @Override
    public void selectTask(TaskItem taskItem) {

    }

    private Observable<DocumentChange> initObservable(Observable<DocumentChange> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    private Observable<DocumentChange> getTodayObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addTodayTasksEventListener(getObservableEventListener(emitter));
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private Observable<DocumentChange> getThisWeekObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addThisWeekTasksEventListener(getObservableEventListener(emitter));
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private Observable<DocumentChange> getOverdueObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addOverdueTasksEventListener(getObservableEventListener(emitter));
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

    private io.reactivex.Observer<DocumentChange> getTodayTasksObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentChange documentChange) {
                TaskModel taskModel = documentChange.getDocument().toObject(TaskModel.class);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<DocumentChange> getThisWeekTasksObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentChange documentChange) {
                TaskModel taskModel = documentChange.getDocument().toObject(TaskModel.class);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private io.reactivex.Observer<DocumentChange> getOverdueTasksObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentChange documentChange) {
                TaskModel taskModel = documentChange.getDocument().toObject(TaskModel.class);
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

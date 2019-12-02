package com.glebworx.pomodoro.ui.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewPresenter;
import com.glebworx.pomodoro.util.PomodoroTimer;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.TaskNotificationManager;
import com.glebworx.pomodoro.util.manager.VibrationManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;

public class ProgressProgressBottomSheetViewPresenter implements IProgressBottomSheetViewPresenter {


    //                                                                                     CONSTANTS

    private static final int PROGRESS_STATUS_IDLE = 0;
    private static final int PROGRESS_STATUS_PAUSED = 1;
    private static final int PROGRESS_STATUS_ACTIVE = 2;

    private static final int DURATION_PERCENT = POMODORO_LENGTH * 600;

    private IProgressBottomSheetView presenterListener;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private PomodoroTimer timer;
    private int progressStatus;
    private int progress;
    private int totalSessions;
    private int completedSessions;
    private VibrationManager vibrationManager;
    private TaskNotificationManager notificationManager;
    private Observable<DocumentSnapshot> taskEventObservable;
    private ListenerRegistration taskEventListenerRegistration;
    private CompositeDisposable compositeDisposable;

    ProgressProgressBottomSheetViewPresenter(@NonNull IProgressBottomSheetView presenterListener, Context context) {
        this.presenterListener = presenterListener;
        init(context);
        initTimer();
    }


    @Override
    public void init(Context context) {

        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;

        vibrationManager = new VibrationManager(context);
        notificationManager = new TaskNotificationManager(context);

        compositeDisposable = new CompositeDisposable();

        taskEventObservable = getTaskEventObservable();
        taskEventObservable = taskEventObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

    @Override
    public synchronized void setTask(ProjectModel projectModel, TaskModel taskModel, int numberOfSessions) {
        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }
        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;
        timer.cancel();
        presenterListener.onClearViews();

        this.projectModel = projectModel;
        this.taskModel = taskModel;
        this.totalSessions = numberOfSessions;
        this.completedSessions = 0;

        presenterListener.onTaskSet(taskModel.getName(), numberOfSessions);

        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }

        taskEventObservable.subscribe(getTaskEventObserver());

        vibrationManager.vibrateMedium();
        notificationManager.showPersistentNotification(taskModel.getName(), TaskNotificationManager.STATUS_READY);
    }

    @Override
    public synchronized void handleStartStopClick() {
        if (progressStatus == PROGRESS_STATUS_ACTIVE) {
            progressStatus = PROGRESS_STATUS_PAUSED;
            timer.pause();
            presenterListener.onTaskPaused();
            vibrationManager.vibrateShort();
            notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_PAUSED, progress);
        } else if (progressStatus == PROGRESS_STATUS_IDLE){
            progressStatus = PROGRESS_STATUS_ACTIVE;
            timer.cancel();
            initTimer();
            timer.start();
            presenterListener.onTaskStarted();
            vibrationManager.vibrateShort();
            notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_WORKING, progress);
        } else {
            progressStatus = PROGRESS_STATUS_ACTIVE;
            timer.resume();
            presenterListener.onTaskResumed();
            vibrationManager.vibrateShort();
            notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_WORKING, progress);
        }
    }

    @Override
    public void cancelSession(Activity activity) {
        if (isStatusIdle()) {
            cancelSession();
        } else {
            showCancelSessionDialog(activity);
        }
        notificationManager.cancelNotification();
    }

    @Override
    public void completeTask(Activity activity) {
        showCompleteTaskDialog(activity);
    }

    @Override
    public boolean isStatusIdle() {
        return progressStatus == PROGRESS_STATUS_IDLE;
    }

    @Override
    public void clearNotifications() {
        notificationManager.cancelAllNotifications();
    }

    private void initTimer() {
        timer = new PomodoroTimer(POMODORO_LENGTH * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                progress = 100 - (int) (millisUntilFinished / DURATION_PERCENT);
                presenterListener.onTick(millisUntilFinished, progress);
                notificationManager.updateNotification(taskModel.getName(), progress);
            }

            @Override
            public void onFinish() {
                completePomodoro();
            }
        };
    }

    private synchronized void completePomodoro() { // TODO need more logic here
        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;
        completedSessions++;
        timer.cancel();
        presenterListener.onClearViews();
        if (taskModel == null) {
            return;
        }
        TaskApi.completePomodoro(
                projectModel,
                taskModel,
                25,
                task -> presenterListener.onPomodoroCompleted(task.isSuccessful(), totalSessions, completedSessions));

        vibrationManager.vibrateLong();
        notificationManager.cancelNotification();
    }

    private synchronized void cancelSession() {
        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }
        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;
        totalSessions = 0;
        completedSessions = 0;
        timer.cancel();
        presenterListener.onClearViews();
        presenterListener.onHideBottomSheet();
    }

    private void showCancelSessionDialog(Activity activity) {
        AlertDialog alertDialog = DialogManager.showDialog(
                activity,
                R.id.container_main,
                R.layout.dialog_generic);
        ((AppCompatTextView) Objects.requireNonNull(alertDialog.findViewById(R.id.text_view_title))).setText(R.string.bottom_sheet_title_cancel_session);
        ((AppCompatTextView) Objects.requireNonNull(alertDialog.findViewById(R.id.text_view_description))).setText(R.string.bottom_sheet_text_cancel_session);
        AppCompatButton positiveButton = alertDialog.findViewById(R.id.button_positive);
        Objects.requireNonNull(positiveButton).setText(R.string.bottom_sheet_title_cancel_session);
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_positive) {
                cancelSession();
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            }
        };
        ((AppCompatButton) Objects.requireNonNull(alertDialog.findViewById(R.id.button_negative))).setOnClickListener(onClickListener);
        positiveButton.setOnClickListener(onClickListener);
    }

    private void showCompleteTaskDialog(Activity activity) {
        AlertDialog alertDialog = DialogManager.showDialog(
                activity,
                R.id.container_main,
                R.layout.dialog_generic);
        ((AppCompatTextView) Objects.requireNonNull(alertDialog.findViewById(R.id.text_view_title))).setText(R.string.bottom_sheet_title_complete_task);
        ((AppCompatTextView) Objects.requireNonNull(alertDialog.findViewById(R.id.text_view_description))).setText(R.string.bottom_sheet_text_complete_task);
        AppCompatButton positiveButton = alertDialog.findViewById(R.id.button_positive);
        Objects.requireNonNull(positiveButton).setText(R.string.bottom_sheet_title_complete_task);
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_positive) {
                completeTask();
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            }
        };
        ((AppCompatButton) Objects.requireNonNull(alertDialog.findViewById(R.id.button_negative))).setOnClickListener(onClickListener);
        positiveButton.setOnClickListener(onClickListener);
    }

    private synchronized void completeTask() {
        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }
        progressStatus = PROGRESS_STATUS_IDLE;
        int progressTemp = progress;
        progress = 0;
        totalSessions = 0;
        completedSessions = 0;
        timer.cancel();
        presenterListener.onClearViews();
        presenterListener.onHideBottomSheet();

        if (taskModel == null) {
            return;
        }

        TaskApi.completeTask(
                projectModel,
                taskModel,
                progressTemp / 4,
                task -> presenterListener.onTaskCompleted(task.isSuccessful()));

        notificationManager.cancelNotification();
        vibrationManager.vibrateLong();

    }

    private Observable<DocumentSnapshot> getTaskEventObservable() {
        return Observable.create(emitter -> {
            taskEventListenerRegistration = TaskApi.addSingleTaskEventListener(
                    projectModel.getName(),
                    taskModel.getName(),
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

    private io.reactivex.Observer<DocumentSnapshot> getTaskEventObserver() {
        return new io.reactivex.Observer<DocumentSnapshot>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentSnapshot documentSnapshot) {
                TaskModel model = documentSnapshot.toObject(TaskModel.class);
                if (model == null) {
                    cancelSession();
                } else {
                    presenterListener.onTaskDataChanged(
                            model.getPomodorosAllocated(),
                            model.getPomodorosCompleted());
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

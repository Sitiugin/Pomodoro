package com.glebworx.pomodoro.ui.view;

import android.content.Context;
import android.os.PowerManager;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewPresenter;
import com.glebworx.pomodoro.util.PomodoroTimer;
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

import static android.content.Context.POWER_SERVICE;
import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;

public class ProgressProgressBottomSheetViewPresenter implements IProgressBottomSheetViewPresenter {


    //                                                                                     CONSTANTS

    private static final Object WAKE_LOCK = new Object();
    private static final String WAKE_LOCK_TAG = "Pomodoro::WakeLockTag";
    private static final int PROGRESS_STATUS_IDLE = 0;
    private static final int PROGRESS_STATUS_PAUSED = 1;
    private static final int PROGRESS_STATUS_ACTIVE = 2;
    private static final int PROGRESS_STATUS_RESTING = 3;

    private static final int DURATION_PERCENT = POMODORO_LENGTH * 600;
    private static final int DURATION_PERCENT_RESTING = POMODORO_LENGTH * 120;


    //                                                                                    ATTRIBUTES

    private PowerManager.WakeLock wakeLock;
    private IProgressBottomSheetView presenterListener;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private PomodoroTimer timer;
    private int progressStatus;
    private int progress;
    private int totalPomodoroCount;
    private int completedPomodoroCount;
    private boolean isResting;
    private VibrationManager vibrationManager;
    private TaskNotificationManager notificationManager;
    private Observable<DocumentSnapshot> taskEventObservable;
    private Observable<DocumentSnapshot> projectEventObservable;
    private ListenerRegistration taskEventListenerRegistration;
    private ListenerRegistration projectEventListenerRegistration;
    private CompositeDisposable compositeDisposable;


    //                                                                                  CONSTRUCTORS

    ProgressProgressBottomSheetViewPresenter(@NonNull IProgressBottomSheetView presenterListener, Context context) {
        this.presenterListener = presenterListener;
        init(context);
        initTimer();
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void init(Context context) {

        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock =
                Objects.requireNonNull(powerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);

        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;

        vibrationManager = new VibrationManager(context);
        notificationManager = new TaskNotificationManager(context);

        notificationManager.cancelAllNotifications();

        compositeDisposable = new CompositeDisposable();

        taskEventObservable = getTaskEventObservable();
        taskEventObservable = taskEventObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        projectEventObservable = getProjectEventObservable();
        projectEventObservable = projectEventObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    @Override
    public void destroy() {
        releaseWakeLock();
        compositeDisposable.clear();
    }

    @Override
    public synchronized void setTask(ProjectModel projectModel, TaskModel taskModel, int numberOfSessions) {

        clearEventListeners();
        clearState();

        this.projectModel = projectModel;
        this.taskModel = taskModel;
        this.totalPomodoroCount = numberOfSessions;
        this.completedPomodoroCount = 0;
        isResting = false;

        presenterListener.onTaskSet(taskModel.getName(), numberOfSessions);

        taskEventObservable.subscribe(getTaskEventObserver());
        projectEventObservable.subscribe(getProjectEventObserver());

        vibrationManager.vibrateMedium();
        notificationManager.showPersistentNotification(taskModel.getName(), TaskNotificationManager.STATUS_READY);
    }

    @Override
    public synchronized void changePomodoroCount(int newPomodoroCount) {
        if (completedPomodoroCount < newPomodoroCount) {
            totalPomodoroCount = newPomodoroCount;
            presenterListener.onPomodoroCountChanged(completedPomodoroCount, totalPomodoroCount, true);
        } else {
            presenterListener.onPomodoroCountChanged(completedPomodoroCount, totalPomodoroCount, false);
        }
    }

    @Override
    public void handleStartStopSkipClick() {
        switch (progressStatus) {
            case PROGRESS_STATUS_ACTIVE:
                pauseTimer();
                break;
            case PROGRESS_STATUS_IDLE:
                startTimer();
                break;
            case PROGRESS_STATUS_PAUSED:
                resumeTimer();
                break;
            case PROGRESS_STATUS_RESTING:
                timer.onFinish();
                break;
        }
    }

    @Override
    public synchronized void closeSession() {

        notificationManager.cancelAllNotifications();

        releaseWakeLock();

        clearState();

        clearEventListeners();

        totalPomodoroCount = 0;
        completedPomodoroCount = 0;
        isResting = false;

        projectModel = null;
        taskModel = null;

        presenterListener.onHideBottomSheet();

    }

    @Override
    public synchronized void completeTask() {

        int progressTemp = progress;

        if (taskModel != null) {
            TaskApi.completeTask(
                    projectModel,
                    taskModel,
                    progressTemp / 4,
                    task -> presenterListener.onTaskCompleted(task.isSuccessful()));
        }

        closeSession();

        vibrationManager.vibrateLong();

    }

    @Override
    public boolean isStatusIdle() {
        return progressStatus == PROGRESS_STATUS_IDLE;
    }

    @Override
    public boolean isStatusResting() {
        return progressStatus == PROGRESS_STATUS_RESTING;
    }

    @Override
    public boolean hasTask() {
        return taskModel != null;
    }

    @Override
    public int getTotalPomodoroCount() {
        return totalPomodoroCount;
    }

    @Override
    public int getCompletedPomodoroCount() {
        return completedPomodoroCount;
    }

    @Override
    public String getTaskName() {
        return taskModel.getName();
    }

    @Override
    public int getRemainingPomodoroCount() {
        return totalPomodoroCount - completedPomodoroCount;
    }

    @Override
    public void clearNotifications() {
        notificationManager.cancelAllNotifications();
    }

    private void initTimer() {

        if (isResting) {

            timer = new PomodoroTimer(POMODORO_LENGTH * 12000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progress = 100 - (int) (millisUntilFinished / DURATION_PERCENT_RESTING);
                    presenterListener.onTick(millisUntilFinished, progress);
                    notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_RESTING, progress);
                }

                @Override
                public synchronized void onFinish() {
                    isResting = false;
                    clearState();
                    startTimer();
                }
            };

        } else {

            timer = new PomodoroTimer(POMODORO_LENGTH * 60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progress = 100 - (int) (millisUntilFinished / DURATION_PERCENT);
                    presenterListener.onTick(millisUntilFinished, progress);
                    notificationManager.updateNotification(taskModel.getName(), progress);
                }

                @Override
                public synchronized void onFinish() {
                    completePomodoro();
                    if (completedPomodoroCount >= totalPomodoroCount) {
                        closeSession();
                    } else {
                        clearState();
                        isResting = true;
                        initTimer();
                        startTimer();
                    }
                }
            };

        }

    }


    //                                                                                       HELPERS

    private synchronized void pauseTimer() {
        progressStatus = PROGRESS_STATUS_PAUSED;
        timer.pause();
        presenterListener.onTaskPaused();
        vibrationManager.vibrateShort();
        notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_PAUSED, progress);
    }

    private synchronized void startTimer() {
        acquireWakeLock();
        progressStatus = this.isResting ? PROGRESS_STATUS_RESTING : PROGRESS_STATUS_ACTIVE;
        timer.cancel();
        initTimer();
        timer.start();
        if (isResting) {
            presenterListener.onRestingPeriodStarted();
            notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_RESTING, progress);
        } else {
            presenterListener.onTaskStarted();
            notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_WORKING, progress);
        }
        vibrationManager.vibrateShort();
    }

    private synchronized void resumeTimer() {
        progressStatus = PROGRESS_STATUS_ACTIVE;
        timer.resume();
        presenterListener.onTaskResumed();
        vibrationManager.vibrateShort();
        notificationManager.updateNotification(taskModel.getName(), TaskNotificationManager.STATUS_WORKING, progress);
    }

    private void completePomodoro() {

        completedPomodoroCount++;

        if (taskModel != null) {
            TaskApi.completePomodoro(
                    projectModel,
                    taskModel,
                    25,
                    task -> presenterListener.onPomodoroCompleted(task.isSuccessful(), totalPomodoroCount, completedPomodoroCount));
        }

        vibrationManager.vibrateLong();

    }

    private void clearEventListeners() {
        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }
        if (projectEventListenerRegistration != null) {
            projectEventListenerRegistration.remove();
            projectEventListenerRegistration = null;
        }
    }

    private Observable<DocumentSnapshot> getTaskEventObservable() {
        return Observable.create(emitter -> {
            taskEventListenerRegistration = TaskApi.addSingleTaskEventListener(
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
                if (!documentSnapshot.exists()) {
                    closeSession();
                    return;
                }
                TaskModel model = documentSnapshot.toObject(TaskModel.class);
                if (model == null) {
                    closeSession();
                    return;
                }
                presenterListener.onTaskDataChanged(
                        model.getPomodorosAllocated(),
                        model.getPomodorosCompleted());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<DocumentSnapshot> getProjectEventObservable() {
        return Observable.create(emitter -> {
            projectEventListenerRegistration = ProjectApi.addProjectEventListener(
                    projectModel.getName(),
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
            emitter.setCancellable(projectEventListenerRegistration::remove);
        });
    }

    private io.reactivex.Observer<DocumentSnapshot> getProjectEventObserver() {
        return new io.reactivex.Observer<DocumentSnapshot>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                compositeDisposable.add(disposable);
            }

            @Override
            public void onNext(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    closeSession();
                    return;
                }
                ProjectModel model = documentSnapshot.toObject(ProjectModel.class);
                if (model == null) {
                    closeSession();
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

    private void clearState() {
        timer.cancel();
        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;
        presenterListener.onClearViews();
        notificationManager.cancelNotification();
    }

    private void acquireWakeLock() {
        synchronized (WAKE_LOCK) {
            if (wakeLock != null && !wakeLock.isHeld()) {
                long timeout = POMODORO_LENGTH * 60000 * (totalPomodoroCount + 1) + POMODORO_LENGTH * 12000 * (totalPomodoroCount);
                try {
                    wakeLock.acquire(timeout); // give extra time to accommodate for breaks
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void releaseWakeLock() {
        synchronized (WAKE_LOCK) {
            if (wakeLock != null && wakeLock.isHeld()) {
                try {
                    wakeLock.release();
                } catch (Exception ignored) {
                }
            }
        }
    }

}

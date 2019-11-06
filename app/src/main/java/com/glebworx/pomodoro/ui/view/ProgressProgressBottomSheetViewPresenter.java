package com.glebworx.pomodoro.ui.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.HistoryApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewPresenter;
import com.glebworx.pomodoro.util.PomodoroTimer;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.SharedPrefsManager;
import com.glebworx.pomodoro.util.manager.TaskNotificationManager;
import com.glebworx.pomodoro.util.manager.VibrationManager;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;

public class ProgressProgressBottomSheetViewPresenter implements IProgressBottomSheetViewPresenter {


    //                                                                                     CONSTANTS

    private static final int PROGRESS_STATUS_IDLE = 0;
    private static final int PROGRESS_STATUS_PAUSED = 1;
    private static final int PROGRESS_STATUS_ACTIVE = 2;

    private static final int DURATION_PERCENT = POMODORO_LENGTH * 600;

    private static final Map<Integer, Integer> CHIP_ID_TO_TARGET_MAP;
    private static final Map<Integer, Integer> TARGET_TO_CHIP_ID_MAP;

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(View.NO_ID, 0);
        map.put(R.id.chip_four, 4);
        map.put(R.id.chip_five, 5);
        map.put(R.id.chip_six, 6);
        map.put(R.id.chip_seven, 7);
        map.put(R.id.chip_eight, 8);
        map.put(R.id.chip_nine, 9);
        map.put(R.id.chip_ten, 10);
        map.put(R.id.chip_eleven, 11);
        map.put(R.id.chip_twelve, 12);
        map.put(R.id.chip_thirteen, 13);
        map.put(R.id.chip_fourteen, 14);
        map.put(R.id.chip_fifteen, 15);
        map.put(R.id.chip_sixteen, 16);
        map.put(R.id.chip_seventeen, 17);
        map.put(R.id.chip_eighteen, 18);
        map.put(R.id.chip_nineteen, 19);
        map.put(R.id.chip_twenty, 20);
        CHIP_ID_TO_TARGET_MAP = Collections.unmodifiableMap(map);
    }

    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, View.NO_ID);
        map.put(4, R.id.chip_four);
        map.put(5, R.id.chip_five);
        map.put(6, R.id.chip_six);
        map.put(7, R.id.chip_seven);
        map.put(8, R.id.chip_eight);
        map.put(9, R.id.chip_nine);
        map.put(10, R.id.chip_ten);
        map.put(11, R.id.chip_eleven);
        map.put(12, R.id.chip_twelve);
        map.put(13, R.id.chip_thirteen);
        map.put(14, R.id.chip_fourteen);
        map.put(15, R.id.chip_fifteen);
        map.put(16, R.id.chip_sixteen);
        map.put(17, R.id.chip_seventeen);
        map.put(18, R.id.chip_eighteen);
        map.put(19, R.id.chip_nineteen);
        map.put(20, R.id.chip_twenty);
        TARGET_TO_CHIP_ID_MAP = Collections.unmodifiableMap(map);
    }

    private Context context;
    private IProgressBottomSheetView presenterListener;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private PomodoroTimer timer;
    private int progressStatus;
    private int progress;
    private VibrationManager vibrationManager;
    private TaskNotificationManager notificationManager;
    private Observable<Integer> todayCountObservable;
    private Observable<DocumentSnapshot> taskEventObservable;
    private ListenerRegistration taskEventListenerRegistration;

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

        todayCountObservable = getCompletedTodayObservable();
        todayCountObservable = todayCountObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        taskEventObservable = getTaskEventObservable();
        taskEventObservable = taskEventObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        todayCountObservable.subscribe(getTodayCountObserver());

    }

    @Override
    public void setTask(ProjectModel projectModel, TaskModel taskModel) {
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
        presenterListener.onTaskSet(taskModel.getName());
        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }
        taskEventObservable.subscribe(getTaskEventObserver());

        vibrationManager.vibrateMedium();
        notificationManager.showPersistentNotification(taskModel.getName(), TaskNotificationManager.STATUS_READY);
    }

    @Override
    public void handleStartStopClick() {
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
    public void showDailyTargetDialog(Context context) {

        if (!(context instanceof Activity)) {
            return;
        }

        AlertDialog alertDialog = DialogManager.showDialog(
                (Activity) context,
                R.id.container_main,
                R.layout.dialog_daily_target);

        ChipGroup chipGroup = alertDialog.findViewById(R.id.chip_group_daily_target);
        AppCompatButton cancelButton = alertDialog.findViewById(R.id.button_negative);
        AppCompatButton setTargetButton = alertDialog.findViewById(R.id.button_positive);

        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);

        int defaultCheckedId = TARGET_TO_CHIP_ID_MAP.get(sharedPrefsManager.getPomodoroTarget());
        if (defaultCheckedId != View.NO_ID) {
            Objects.requireNonNull(chipGroup).check(defaultCheckedId);
        }

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_positive) {
                int pomodoroTarget = CHIP_ID_TO_TARGET_MAP.get(Objects.requireNonNull(chipGroup).getCheckedChipId());
                sharedPrefsManager.setPomodoroTarget(pomodoroTarget);
                alertDialog.dismiss();
                int messageId = pomodoroTarget == 0
                        ? R.string.bottom_sheet_toast_pomodoro_target_removed
                        : defaultCheckedId == View.NO_ID
                        ? R.string.bottom_sheet_toast_pomodoro_target_set
                        : R.string.bottom_sheet_toast_pomodoro_target_changed;
                Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
            }
        };

        Objects.requireNonNull(cancelButton).setOnClickListener(onClickListener);
        Objects.requireNonNull(setTargetButton).setOnClickListener(onClickListener);

        alertDialog.show();

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

    private void completePomodoro() { // TODO need more logic here
        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;
        timer.cancel();
        presenterListener.onClearViews();
        if (taskModel == null) {
            return;
        }
        TaskApi.completePomodoro(
                projectModel,
                taskModel,
                task -> presenterListener.onPomodoroCompleted(task.isSuccessful()));

        vibrationManager.vibrateLong();
        notificationManager.cancelNotification();
    }

    private void cancelSession() {
        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }
        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;
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

    private void completeTask() {
        if (taskEventListenerRegistration != null) {
            taskEventListenerRegistration.remove();
            taskEventListenerRegistration = null;
        }
        progressStatus = PROGRESS_STATUS_IDLE;
        progress = 0;
        timer.cancel();
        presenterListener.onClearViews();
        presenterListener.onHideBottomSheet();

        if (taskModel == null) {
            return;
        }

        TaskApi.completeTask(
                projectModel,
                taskModel,
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
            public void onSubscribe(Disposable d) {

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

    private Observable<Integer> getCompletedTodayObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = HistoryApi.addTodayEventListener((querySnapshot, e) -> {
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
            });
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    private io.reactivex.Observer<Integer> getTodayCountObserver() {
        return new io.reactivex.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer todayCount) {
                presenterListener.onTodayCountUpdated(todayCount);
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

package com.glebworx.pomodoro.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

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
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

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

    private IProgressBottomSheetView presenterListener;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private PomodoroTimer timer;
    private int progressStatus;
    private @NonNull
    Observable<Integer> todayCountObservable;
    Observable<DocumentSnapshot> taskEventObservable;

    ProgressProgressBottomSheetViewPresenter(@NonNull IProgressBottomSheetView presenterListener) {
        this.presenterListener = presenterListener;
        init();
        initTimer();
    }


    @Override
    public void init() {
        progressStatus = PROGRESS_STATUS_IDLE;
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
    }

    @Override
    public void subscribe() {
        todayCountObservable.subscribe(getTodayCountObserver());
    }

    @Override
    public void unsubscribe() {
        //todayCountObservable = todayCountObservable;
        //taskEventObservable = taskEventObservable;
    }

    @Override
    public void setTask(ProjectModel projectModel, TaskModel taskModel) {
        progressStatus = PROGRESS_STATUS_IDLE;
        this.projectModel = projectModel;
        this.taskModel = taskModel;
        presenterListener.onTaskSet(taskModel.getName());
        taskEventObservable.subscribe(getTaskEventObserver());
    }

    @Override
    public void handleStartStopClick() {
        if (progressStatus == PROGRESS_STATUS_ACTIVE) {
            progressStatus = PROGRESS_STATUS_PAUSED;
            timer.pause();
            presenterListener.onTaskPaused();
        } else if (progressStatus == PROGRESS_STATUS_IDLE){
            progressStatus = PROGRESS_STATUS_ACTIVE;
            timer.cancel();
            initTimer();
            timer.start();
            presenterListener.onTaskStarted();
        } else {
            progressStatus = PROGRESS_STATUS_ACTIVE;
            timer.resume();
            presenterListener.onTaskResumed();
        }
    }

    @Override
    public void cancelTask() {
        progressStatus = PROGRESS_STATUS_IDLE;
        timer.cancel();
        presenterListener.onClearViews();
        presenterListener.onHideBottomSheet();
    }

    @Override
    public void completeTask() {
        completePomodoro();
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

        SparseIntArray chipIdToTargetMap = getChipIdToTargetMap();
        SparseIntArray targetToChipIdMap = getTargetToChipIdMap();

        int defaultCheckedId = targetToChipIdMap.get(sharedPrefsManager.getPomodoroTarget());
        if (defaultCheckedId != View.NO_ID) {
            Objects.requireNonNull(chipGroup).check(defaultCheckedId);
        }

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_positive) {
                int pomodoroTarget = chipIdToTargetMap.get(Objects.requireNonNull(chipGroup).getCheckedChipId());
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

    private void initTimer() {
        timer = new PomodoroTimer(POMODORO_LENGTH * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                presenterListener.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                completePomodoro();
            }
        };
    }

    private void completePomodoro() {
        progressStatus = PROGRESS_STATUS_IDLE;
        presenterListener.onClearViews();
        if (taskModel == null) {
            return;
        }
        taskModel.addPomodoro();
        TaskApi.completePomodoro(
                projectModel,
                taskModel,
                task -> presenterListener.onPomodoroCompleted(
                        task.isSuccessful()));
    }

    private SparseIntArray getChipIdToTargetMap() {
        SparseIntArray chipIdMap = new SparseIntArray();
        chipIdMap.put(View.NO_ID, 0);
        chipIdMap.put(R.id.chip_four, 4);
        chipIdMap.put(R.id.chip_five, 5);
        chipIdMap.put(R.id.chip_six, 6);
        chipIdMap.put(R.id.chip_seven, 7);
        chipIdMap.put(R.id.chip_eight, 8);
        chipIdMap.put(R.id.chip_nine, 9);
        chipIdMap.put(R.id.chip_ten, 10);
        chipIdMap.put(R.id.chip_eleven, 11);
        chipIdMap.put(R.id.chip_twelve, 12);
        chipIdMap.put(R.id.chip_thirteen, 13);
        chipIdMap.put(R.id.chip_fourteen, 14);
        chipIdMap.put(R.id.chip_fifteen, 15);
        chipIdMap.put(R.id.chip_sixteen, 16);
        chipIdMap.put(R.id.chip_seventeen, 17);
        chipIdMap.put(R.id.chip_eighteen, 18);
        chipIdMap.put(R.id.chip_nineteen, 19);
        chipIdMap.put(R.id.chip_twenty, 20);
        return chipIdMap;
    }

    private SparseIntArray getTargetToChipIdMap() {
        SparseIntArray chipIdMap = new SparseIntArray();
        chipIdMap.put(0, View.NO_ID);
        chipIdMap.put(4, R.id.chip_four);
        chipIdMap.put(5, R.id.chip_five);
        chipIdMap.put(6, R.id.chip_six);
        chipIdMap.put(7, R.id.chip_seven);
        chipIdMap.put(8, R.id.chip_eight);
        chipIdMap.put(9, R.id.chip_nine);
        chipIdMap.put(10, R.id.chip_ten);
        chipIdMap.put(11, R.id.chip_eleven);
        chipIdMap.put(12, R.id.chip_twelve);
        chipIdMap.put(13, R.id.chip_thirteen);
        chipIdMap.put(14, R.id.chip_fourteen);
        chipIdMap.put(15, R.id.chip_fifteen);
        chipIdMap.put(16, R.id.chip_sixteen);
        chipIdMap.put(17, R.id.chip_seventeen);
        chipIdMap.put(18, R.id.chip_eighteen);
        chipIdMap.put(19, R.id.chip_nineteen);
        chipIdMap.put(20, R.id.chip_twenty);
        return chipIdMap;
    }

    private Observable<DocumentSnapshot> getTaskEventObservable() {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addSingleTaskEventListener(
                    projectModel.getName(),
                    taskModel.getName(),
                    (documentSnapshot, e) -> {
                        if (e != null) {
                            emitter.onError(e);
                            return;
                        }
                        if (documentSnapshot == null) {
                            return;
                        }
                        emitter.onNext(documentSnapshot);
                    });
            emitter.setCancellable(listenerRegistration::remove);
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
                    cancelTask();
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
                if (e != null) {
                    emitter.onError(e);
                    return;
                }
                if (querySnapshot == null || querySnapshot.isEmpty()) {
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

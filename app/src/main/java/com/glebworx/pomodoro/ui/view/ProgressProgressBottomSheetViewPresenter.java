package com.glebworx.pomodoro.ui.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewPresenter;
import com.glebworx.pomodoro.util.PomodoroTimer;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.SharedPrefsManager;
import com.google.android.material.chip.ChipGroup;

import java.util.Objects;

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
    private int pomodoroTarget;

    ProgressProgressBottomSheetViewPresenter(@NonNull IProgressBottomSheetView presenterListener) {
        this.presenterListener = presenterListener;
        init();
        initTimer();
    }


    @Override
    public void init() {
        progressStatus = PROGRESS_STATUS_IDLE;
    }

    @Override
    public void setTask(ProjectModel projectModel, TaskModel taskModel) {
        progressStatus = PROGRESS_STATUS_IDLE;
        this.projectModel = projectModel;
        this.taskModel = taskModel;
        presenterListener.onTaskSet(taskModel.getName());
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

        int defaultCheckedId = getIdFromPomodoroTarget(sharedPrefsManager.getPomodoroTarget());
        if (defaultCheckedId != View.NO_ID) {
            Objects.requireNonNull(chipGroup).check(defaultCheckedId);
        }

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_positive) {
                int pomodoroTarget = getPomodoroTargetFromId(Objects.requireNonNull(chipGroup).getCheckedChipId());
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

    private int getPomodoroTargetFromId(int checkedId) {
        switch (checkedId) {
            case R.id.chip_four:
                return 4;
            case R.id.chip_five:
                return 5;
            case R.id.chip_six:
                return 6;
            case R.id.chip_seven:
                return 7;
            case R.id.chip_eight:
                return 8;
            case R.id.chip_nine:
                return 9;
            case R.id.chip_ten:
                return 10;
            case R.id.chip_eleven:
                return 11;
            case R.id.chip_twelve:
                return 12;
            case R.id.chip_thirteen:
                return 13;
            case R.id.chip_fourteen:
                return 14;
            case R.id.chip_fifteen:
                return 15;
            case R.id.chip_sixteen:
                return 16;
            case R.id.chip_seventeen:
                return 17;
            case R.id.chip_eighteen:
                return 18;
            case R.id.chip_nineteen:
                return 19;
            case R.id.chip_twenty:
                return 20;
            default:
                return 0;

        }
    }

    private int getIdFromPomodoroTarget(int target) {
        switch (target) {
            case 4:
                return R.id.chip_four;
            case 5:
                return R.id.chip_five;
            case 6:
                return R.id.chip_six;
            case 7:
                return R.id.chip_seven;
            case 8:
                return R.id.chip_eight;
            case 9:
                return R.id.chip_nine;
            case 10:
                return R.id.chip_ten;
            case 11:
                return R.id.chip_eleven;
            case 12:
                return R.id.chip_twelve;
            case 13:
                return R.id.chip_thirteen;
            case 14:
                return R.id.chip_fourteen;
            case 15:
                return R.id.chip_fifteen;
            case 16:
                return R.id.chip_sixteen;
            case 17:
                return R.id.chip_seventeen;
            case 18:
                return R.id.chip_eighteen;
            case 19:
                return R.id.chip_nineteen;
            case 20:
                return R.id.chip_twenty;
            default:
                return View.NO_ID;

        }
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
        TaskApi.completePomodoro(projectModel, taskModel, task -> presenterListener.onPomodoroCompleted(task.isSuccessful()));
    }

}

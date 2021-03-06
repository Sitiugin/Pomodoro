package com.glebworx.pomodoro.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.about.AboutFragment;
import com.glebworx.pomodoro.ui.fragment.about.interfaces.IAboutFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.add_project.AddProjectFragment;
import com.glebworx.pomodoro.ui.fragment.add_project.interfaces.IAddProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.add_task.AddTaskFragment;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.archive.ArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.projects.ProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.report.ReportFragment;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IReportFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.report_project.ReportProjectFragment;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_project.ViewProjectFragment;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_tasks.ViewTasksFragment;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentInteractionListener;
import com.glebworx.pomodoro.ui.view.ProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewInteractionListener;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.NumberPickerManager;
import com.glebworx.pomodoro.util.manager.TaskNotificationManager;
import com.glebworx.pomodoro.util.manager.TransitionFragmentManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.glebworx.pomodoro.util.constants.Constants.MAX_POMODOROS_SESSION;


// TODO observable error - log
public class MainActivity
        extends AppCompatActivity
        implements
        IProjectsFragmentInteractionListener,
        IAddProjectFragmentInteractionListener,
        IViewProjectFragmentInteractionListener,
        IReportProjectFragmentInteractionListener,
        IViewTasksFragmentInteractionListener,
        IAddTaskFragmentInteractionListener,
        IReportFragmentInteractionListener,
        IAboutFragmentInteractionListener,
        IArchiveFragmentInteractionListener,
        IProgressBottomSheetViewInteractionListener {


    //                                                                                       BINDING

    @BindView(R.id.bottom_sheet) ProgressBottomSheetView bottomSheetView;


    //                                                                                    ATTRIBUTES

    private TransitionFragmentManager fragmentManager;
    private BottomSheetBehavior bottomSheetBehavior;
    private Unbinder unbinder;

    //                                                                                     LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TaskNotificationManager.createNotificationChannel(MainActivity.this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        fragmentManager =
                new TransitionFragmentManager(getSupportFragmentManager(), R.id.container_main);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);

        initBottomSheet();

        fragmentManager.addRootFragment(ProjectsFragment.newInstance());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomSheetView.onClearViews();
        fragmentManager.clearAllFragments();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (fragmentManager.hasStackedFragments()) {
            fragmentManager.popFromBackStack();
        } else {
            super.onBackPressed();
        }
    }


    //                                                                          NAVIGATION CALLBACKS

    @Override
    public void onViewReport() {
        fragmentManager.pushToBackStack(ReportFragment.newInstance());
    }

    @Override
    public void onAddProject() {
        fragmentManager.pushToBackStack(AddProjectFragment.newInstance());
    }

    @Override
    public void onEditProject(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(AddProjectFragment.newInstance(projectModel));
    }

    @Override
    public void onViewProject(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(ViewProjectFragment.newInstance(projectModel));
    }

    @Override
    public void onViewTodayTasks() {
        fragmentManager.pushToBackStack(ViewTasksFragment.newInstance(ViewTasksFragment.TYPE_TODAY));
    }

    @Override
    public void onViewThisWeekTasks() {
        fragmentManager.pushToBackStack(ViewTasksFragment.newInstance(ViewTasksFragment.TYPE_THIS_WEEK));
    }

    @Override
    public void onViewOverdueTasks() {
        fragmentManager.pushToBackStack(ViewTasksFragment.newInstance(ViewTasksFragment.TYPE_OVERDUE));
    }

    @Override
    public void onViewAboutInfo() {
        fragmentManager.pushToBackStack(AboutFragment.newInstance());
    }

    @Override
    public void onViewProjectArchive() {
        fragmentManager.pushToBackStack(ArchiveFragment.newInstance());
    }

    @Override
    public void onAddTask(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(AddTaskFragment.newInstance(projectModel));
    }

    @Override
    public void onEditTask(ProjectModel projectModel, TaskModel taskModel) {
        fragmentManager.pushToBackStack(AddTaskFragment.newInstance(projectModel, taskModel));
    }

    @Override
    public void onSelectTask(ProjectModel projectModel, TaskModel taskModel) {
        if (taskModel.isCompleted()) {
            return;
        }
        showSetTaskDialog(projectModel, taskModel);
    }

    @Override
    public void onViewProjectReport(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(ReportProjectFragment.newInstance(projectModel));
    }

    @Override
    public void onHideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCloseFragment() {
        fragmentManager.popFromBackStack();
    }

    @Override
    public void onSignOut() {
        if (!bottomSheetView.getPresenter().isStatusIdle()) {
            bottomSheetView.getPresenter().closeSession();
        }
        if (AuthManager.getInstance().isSignedIn()) {
            AuthManager.getInstance().signOut();
            Toast.makeText(MainActivity.this, R.string.core_signed_out, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, R.string.core_sign_out_failed, Toast.LENGTH_LONG).show();
        }
    }


    //                                                                                       HELPERS

    private void initBottomSheet() {

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetView.expandBottomSheetViews();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetView.collapseBottomSheetViews();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetView.setOnClickListener(view -> {
            int state = bottomSheetBehavior.getState();
            if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

    }

    private void showSetTaskDialog(ProjectModel projectModel, TaskModel taskModel) {

        AlertDialog alertDialog = DialogManager.buildDialog(
                MainActivity.this,
                R.id.container_main,
                R.layout.dialog_set_task);

        alertDialog.show();

        NumberPicker picker = alertDialog.findViewById(R.id.number_picker);
        NumberPickerManager.initPicker(
                MainActivity.this,
                Objects.requireNonNull(picker),
                1,
                MAX_POMODOROS_SESSION);

        AppCompatButton positiveButton = alertDialog.findViewById(R.id.button_positive);

        boolean isReplace = bottomSheetView.getPresenter().hasTask();
        int pomodoroCount;

        if (isReplace) {

            ((AppCompatTextView) Objects.requireNonNull(
                    alertDialog.findViewById(R.id.text_view_title))).setText(R.string.main_title_replace_task);
            Spanned description = Html.fromHtml(
                    getString(R.string.main_text_replace_task, taskModel.getName()), 0);
            ((AppCompatTextView) Objects.requireNonNull(
                    alertDialog.findViewById(R.id.text_view_description))).setText(description);
            Objects.requireNonNull(positiveButton).setText(R.string.main_title_replace_task);
            pomodoroCount = bottomSheetView.getPresenter().getRemainingPomodoroCount();

        } else {

            Spanned description = Html.fromHtml(
                    getString(R.string.main_text_set_task, taskModel.getName()), 0);
            ((AppCompatTextView) Objects.requireNonNull(
                    alertDialog.findViewById(R.id.text_view_description))).setText(description);
            pomodoroCount = taskModel.getPomodorosAllocated();

        }

        if (pomodoroCount < 1) {
            pomodoroCount = 1;
        } else if (pomodoroCount > MAX_POMODOROS_SESSION) {
            pomodoroCount = MAX_POMODOROS_SESSION;
        }
        picker.setValue(pomodoroCount);

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_positive) {
                setTask(projectModel, taskModel, picker.getValue());
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            }
        };

        ((AppCompatButton) Objects.requireNonNull(alertDialog.findViewById(R.id.button_negative))).setOnClickListener(onClickListener);
        Objects.requireNonNull(positiveButton).setOnClickListener(onClickListener);

    }

    private void setTask(ProjectModel projectModel,
                         TaskModel taskModel,
                         int numberOfSessions) {
        bottomSheetView.getPresenter().setTask(projectModel, taskModel, numberOfSessions);
        bottomSheetView.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}

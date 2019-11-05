package com.glebworx.pomodoro.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.add_project.AddProjectFragment;
import com.glebworx.pomodoro.ui.fragment.add_project.interfaces.IAddProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.add_task.AddTaskFragment;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.projects.ProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.report.ReportFragment;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IReportFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.settings.interfaces.ISettingsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_project.ViewProjectFragment;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.view.ProgressBottomSheetView;
import com.glebworx.pomodoro.ui.view.interfaces.IProgressBottomSheetViewInteractionListener;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.TaskNotificationManager;
import com.glebworx.pomodoro.util.manager.TransitionFragmentManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity
        extends AppCompatActivity
        implements
            IProjectsFragmentInteractionListener,
            IAddProjectFragmentInteractionListener,
            IViewProjectFragmentInteractionListener,
            IAddTaskFragmentInteractionListener,
            IReportFragmentInteractionListener,
            ISettingsFragmentInteractionListener,
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

        fragmentManager =
                new TransitionFragmentManager(getSupportFragmentManager(), R.id.container_main);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);

        initBottomSheet();

        fragmentManager.addRootFragment(ProjectsFragment.newInstance());

        TaskNotificationManager notificationManager = new TaskNotificationManager(MainActivity.this);
        notificationManager.showPersistentNotification("Some Task", "Started");

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
        // TODO implement
        Toast.makeText(this, "Today clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewThisWeekTasks() {
        // TODO implement
        Toast.makeText(this, "This week clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewOverdueTasks() {
        // TODO implement
        Toast.makeText(this, "Overdue clicked", Toast.LENGTH_SHORT).show();
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
        if (bottomSheetView.getPresenter().isStatusIdle()) {
            setTask(projectModel, taskModel);
        } else {
            showReplaceTaskDialog(projectModel, taskModel);
        }
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

    private void setTask(ProjectModel projectModel, TaskModel taskModel) {
        bottomSheetView.setVisibility(View.VISIBLE);
        bottomSheetView.getPresenter().setTask(projectModel, taskModel);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void showReplaceTaskDialog(ProjectModel projectModel, TaskModel taskModel) {
        AlertDialog alertDialog = DialogManager.showDialog(
                MainActivity.this,
                R.id.container_main,
                R.layout.dialog_generic);
        ((AppCompatTextView) Objects.requireNonNull(alertDialog.findViewById(R.id.text_view_title))).setText(R.string.main_title_replace_task);
        ((AppCompatTextView) Objects.requireNonNull(alertDialog.findViewById(R.id.text_view_description))).setText(getString(R.string.main_text_replace_task, taskModel.getName()));
        AppCompatButton positiveButton = alertDialog.findViewById(R.id.button_positive);
        Objects.requireNonNull(positiveButton).setText(R.string.main_title_replace);
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_positive) {
                setTask(projectModel, taskModel);
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            }
        };
        ((AppCompatButton) Objects.requireNonNull(alertDialog.findViewById(R.id.button_negative))).setOnClickListener(onClickListener);
        positiveButton.setOnClickListener(onClickListener);
    }

}

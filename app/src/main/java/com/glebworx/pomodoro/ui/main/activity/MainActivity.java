package com.glebworx.pomodoro.ui.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.main.fragment.AddProjectFragment;
import com.glebworx.pomodoro.ui.main.fragment.AddTaskFragment;
import com.glebworx.pomodoro.ui.main.fragment.ProjectsFragment;
import com.glebworx.pomodoro.ui.main.fragment.ReportFragment;
import com.glebworx.pomodoro.ui.main.fragment.ViewProjectFragment;
import com.glebworx.pomodoro.ui.main.view.ProgressBottomSheetView;
import com.glebworx.pomodoro.util.PomodoroTimer;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.TransitionFragmentManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;


// TODO don't forget to unbind
public class MainActivity
        extends AppCompatActivity
        implements ProjectsFragment.OnProjectFragmentInteractionListener,
                    AddProjectFragment.OnAddProjectFragmentInteractionListener,
                    ViewProjectFragment.OnViewProjectFragmentInteractionListener,
                    AddTaskFragment.OnAddTaskFragmentInteractionListener,
                    ProgressBottomSheetView.OnBottomSheetInteractionListener {


    //                                                                                       BINDING

    @BindView(R.id.bottom_sheet) ProgressBottomSheetView bottomSheet;


    //                                                                                    ATTRIBUTES

    private TransitionFragmentManager fragmentManager;
    private BottomSheetBehavior bottomSheetBehavior;


    //                                                                                     LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragmentManager =
                new TransitionFragmentManager(getSupportFragmentManager(), R.id.container_main);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        initBottomSheet();

        fragmentManager.addRootFragment(ProjectsFragment.newInstance());

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
    public void onAddProject() {
        fragmentManager.pushToBackStack(AddProjectFragment.newInstance());
    }

    @Override
    public void onViewProject(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(ViewProjectFragment.newInstance(projectModel));
    }

    @Override
    public void onViewReport() {
        fragmentManager.pushToBackStack(ReportFragment.newInstance());
    }

    @Override
    public void onCloseFragment() {
        fragmentManager.popFromBackStack();
    }

    @Override
    public void onEditProject(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(AddProjectFragment.newInstance(projectModel));
    }

    @Override
    public void onAddTask(ProjectModel projectModel) {
        fragmentManager.pushToBackStack(AddTaskFragment.newInstance(projectModel));
    }

    @Override
    public void onSelectTask(TaskModel taskModel) {
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheet.setTask(taskModel);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onEditTask(ProjectModel projectModel, TaskModel taskModel) {
        fragmentManager.pushToBackStack(AddTaskFragment.newInstance(projectModel, taskModel));
    }

    @Override
    public void onCancelTask(TaskModel taskModel) {

    }

    @Override
    public void onCompleteTask(TaskModel taskModel) {

    }


    //                                                                                       HELPERS

    private void initBottomSheet() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheet.expandBottomSheetViews();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheet.collapseBottomSheetViews();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) { }
        });
        bottomSheet.setOnClickListener(view -> {
            int state = bottomSheetBehavior.getState();
            if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

}

package com.glebworx.pomodoro.ui.main.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.item.AddItem;
import com.glebworx.pomodoro.item.ProjectHeaderItem;
import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.ui.main.fragment.ProjectsFragment;
import com.glebworx.pomodoro.ui.main.fragment.ReportFragment;
import com.glebworx.pomodoro.ui.main.fragment.TasksFragment;
import com.glebworx.pomodoro.ui.report.ReportActivity;
import com.glebworx.pomodoro.util.DummyDataProvider;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.ConstraintTransitionManager;
import com.glebworx.pomodoro.util.manager.NavigationFragmentManager;
import com.glebworx.pomodoro.util.manager.TransitionFragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.glebworx.pomodoro.util.constants.Constants.ANIM_DURATION;


public class MainActivity extends AppCompatActivity {


    //                                                                                       BINDING

    @BindView(R.id.bottom_sheet) ConstraintLayout bottomSheet;


    //                                                                                    ATTRIBUTES

    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintSet constraintSet;
    private TransitionFragmentManager fragmentManager;

    //                                                                                     LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        constraintSet = new ConstraintSet();
        fragmentManager =
                new TransitionFragmentManager(getSupportFragmentManager(), R.id.container_main);

        initBottomSheet();
        initClickEvents();
        addProjectsFragment();

    }

    private void initBottomSheet() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    expandBottomSheetViews();
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    collapseBottomSheetViews();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) { }
        });
    }

    private void expandBottomSheetViews() {

        TransitionManager.beginDelayedTransition(bottomSheet);
        constraintSet.clone(bottomSheet);

        // animate seek arc
        constraintSet.setVisibility(R.id.seek_arc, ConstraintSet.VISIBLE);

        // animate remaining time
        constraintSet.setVisibility(R.id.text_view_time_remaining, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.text_view_time_remaining_large, ConstraintSet.VISIBLE);

        // animate status
        constraintSet.connect(
                R.id.text_view_status,
                ConstraintSet.TOP,
                R.id.text_view_time_remaining_large,
                ConstraintSet.BOTTOM);
        constraintSet.connect(
                R.id.text_view_status,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END);

        // animate task
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.TOP,
                R.id.text_view_status,
                ConstraintSet.BOTTOM);
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.VISIBLE);

        constraintSet.applyTo(bottomSheet);

    }

    private void collapseBottomSheetViews() {

        TransitionManager.beginDelayedTransition(bottomSheet);
        constraintSet.clone(bottomSheet);

        // animate seek arc
        constraintSet.setVisibility(R.id.seek_arc, ConstraintSet.INVISIBLE);

        // animate remaining time
        constraintSet.setVisibility(R.id.text_view_time_remaining, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.text_view_time_remaining_large, ConstraintSet.INVISIBLE);

        // animate status
        constraintSet.connect(
                R.id.text_view_status,
                ConstraintSet.TOP,
                R.id.text_view_task,
                ConstraintSet.BOTTOM);
        constraintSet.clear(R.id.text_view_status, ConstraintSet.END);

        // animate task
        constraintSet.connect(
                R.id.text_view_task,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        constraintSet.clear(R.id.text_view_task, ConstraintSet.END);

        // animate buttons
        constraintSet.setVisibility(R.id.button_start_stop, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.fab_start_stop_large, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_cancel, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_complete, ConstraintSet.INVISIBLE);

        constraintSet.applyTo(bottomSheet);

    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.bottom_sheet) {
                int state = bottomSheetBehavior.getState();
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        };
        bottomSheet.setOnClickListener(onClickListener);
    }

    private void addProjectsFragment() {
        fragmentManager.addRootFragment(new ProjectsFragment());
    }

}

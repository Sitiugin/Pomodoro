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

    @BindView(R.id.button_report) AppCompatImageButton reportButton;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.bottom_sheet) ConstraintLayout bottomSheet;


    //                                                                                    ATTRIBUTES

    private ItemAdapter<ProjectItem> projectAdapter;
    private BottomSheetBehavior bottomSheetBehavior;
    private ConstraintSet constraintSet;

    //                                                                                     LIFECYCLE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        projectAdapter = new ItemAdapter<>();
        projectAdapter.add(DummyDataProvider.getProjects());
        FastAdapter fastAdapter = new FastAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        constraintSet = new ConstraintSet();

        initRecyclerView(layoutManager, fastAdapter);
        initSearchView();
        initBottomSheet();
        initClickEvents();

    }

    private void initRecyclerView(LinearLayoutManager layoutManager, FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));

        ItemAdapter<ProjectHeaderItem> headerAdapter = new ItemAdapter<>();
        headerAdapter.add(new ProjectHeaderItem());
        ItemAdapter<AddItem> footerAdapter = new ItemAdapter<>();
        footerAdapter.add(new AddItem(getString(R.string.main_title_add_project)));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, projectAdapter);
        fastAdapter.addAdapter(2, footerAdapter);

        fastAdapter.setHasStableIds(true);
        recyclerView.setAdapter(fastAdapter);
    }

    private void initSearchView() {

        IItemAdapter.Predicate<ProjectItem> predicate = (item, constraint) -> {
            if (constraint == null) {
                return true;
            }
            String title = item.getModel().getName();
            return title != null && title.toLowerCase().contains(constraint);
        };

        ItemFilter<ProjectItem, ProjectItem> itemFilter =
                new ItemFilter<ProjectItem, ProjectItem>(projectAdapter).withFilterPredicate(predicate);

        projectAdapter.withItemFilter(itemFilter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                projectAdapter.filter(newText);
                return true;
            }
        });

    }

    private void initBottomSheet() {
        ConstraintTransitionManager transitionManager = new ConstraintTransitionManager();
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
        constraintSet.clone(MainActivity.this, R.layout.view_bottom_sheet_expanded);
        constraintSet.setAlpha(R.id.seek_arc, 100);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(ANIM_DURATION);
        TransitionManager.beginDelayedTransition(bottomSheet, transition);
        constraintSet.applyTo(bottomSheet);
    }

    private void collapseBottomSheetViews() {
        constraintSet.clone(MainActivity.this, R.layout.view_bottom_sheet_collapsed);
        constraintSet.setAlpha(R.id.seek_arc, 0);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(ANIM_DURATION);
        TransitionManager.beginDelayedTransition(bottomSheet, transition);
        constraintSet.applyTo(bottomSheet);
    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.bottom_sheet:
                    int state = bottomSheetBehavior.getState();
                    if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else if (state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    break;
                case R.id.button_report:
                    break;
                case R.id.button_options:
                    break;
            }
        };
        bottomSheet.setOnClickListener(onClickListener);
        reportButton.setOnClickListener(onClickListener);
        optionsButton.setOnClickListener(onClickListener);
    }

}

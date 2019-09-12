package com.glebworx.pomodoro.ui.main.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.item.AddItem;
import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.ui.main.fragment.ProjectsFragment;
import com.glebworx.pomodoro.ui.main.fragment.ReportFragment;
import com.glebworx.pomodoro.ui.main.fragment.TasksFragment;
import com.glebworx.pomodoro.ui.report.ReportActivity;
import com.glebworx.pomodoro.util.DummyDataProvider;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.NavigationFragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {


    //                                                                                       BINDING

    @BindView(R.id.button_report) AppCompatImageButton reportButton;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    //                                                                                    ATTRIBUTES

    private ItemAdapter<ProjectItem> projectAdapter;

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

        initRecyclerView(layoutManager, fastAdapter);
        initSearchView();
        initClickEvents();

    }

    private void initRecyclerView(LinearLayoutManager layoutManager, FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));

        fastAdapter.addAdapter(0, projectAdapter);
        ItemAdapter<AddItem> footerAdapter = new ItemAdapter<>();
        footerAdapter.add(new AddItem(getString(R.string.main_title_add_project)));
        fastAdapter.addAdapter(1, footerAdapter);
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

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_report) {
                startActivity(new Intent(MainActivity.this, ReportActivity.class));
            } else {
                // TODO open options
            }
        };
        reportButton.setOnClickListener(onClickListener);
        optionsButton.setOnClickListener(onClickListener);
    }

}

package com.glebworx.pomodoro.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.manager.NavigationFragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {


    //                                                                                       BINDING

    @BindView(R.id.bottom_navigation_view) BottomNavigationView bottomNavigationView;


    //                                                                                     CONSTANTS

    private static final String TAG_TASKS = "tag_tasks";
    private static final String TAG_PROJECTS = "tag_projects";
    private static final String TAG_REPORT = "tag_report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initBottomNavigationView();
    }

    private void initBottomNavigationView() {

        Map<String, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(TAG_TASKS, new TasksFragment());
        fragmentMap.put(TAG_PROJECTS, new ProjectsFragment());
        fragmentMap.put(TAG_REPORT, new ReportFragment());

        NavigationFragmentManager navigationFragmentManager = new NavigationFragmentManager(
                getSupportFragmentManager(),
                R.id.container_main,
                fragmentMap);

        bottomNavigationView.setSelectedItemId(R.id.navigation_tasks);
        navigationFragmentManager.selectFragment(TAG_TASKS);
        bottomNavigationView.setOnNavigationItemSelectedListener(getNavListener(navigationFragmentManager));

    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            getNavListener(NavigationFragmentManager manager) {

        return item -> {

            switch (item.getItemId()) {

                case R.id.navigation_tasks:
                    manager.selectFragment(TAG_TASKS);
                    return true;

                case R.id.navigation_projects:
                    manager.selectFragment(TAG_PROJECTS);
                    return true;

                case R.id.navigation_report:
                    manager.selectFragment(TAG_REPORT);
                    return true;

            }

            return false;

        };

    }
}

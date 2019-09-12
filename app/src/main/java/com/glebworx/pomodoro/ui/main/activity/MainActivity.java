package com.glebworx.pomodoro.ui.main.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.main.fragment.ProjectsFragment;
import com.glebworx.pomodoro.ui.main.fragment.ReportFragment;
import com.glebworx.pomodoro.ui.main.fragment.TasksFragment;
import com.glebworx.pomodoro.util.manager.NavigationFragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {


    //                                                                                       BINDING


    //                                                                                     CONSTANTS

    private static final String TAG_TASKS = "tag_tasks";
    private static final String TAG_PROJECTS = "tag_projects";
    private static final String TAG_REPORT = "tag_report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

}

package com.glebworx.pomodoro.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;


public class ProjectsFragment extends Fragment {


    public ProjectsFragment() { }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_projects, container, false);

    }

}

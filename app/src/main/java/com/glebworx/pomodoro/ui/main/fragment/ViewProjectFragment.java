package com.glebworx.pomodoro.ui.main.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;


public class ViewProjectFragment extends Fragment {

    private static final String ARG_PROJECT_MODEL = "project_model";

    public ViewProjectFragment() { }

    public static ViewProjectFragment newInstance(ProjectModel projectModel) {
        ViewProjectFragment fragment = new ViewProjectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_project, container, false);

        return rootView;
    }

}

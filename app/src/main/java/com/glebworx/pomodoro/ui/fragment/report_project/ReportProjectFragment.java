package com.glebworx.pomodoro.ui.fragment.report_project;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragment;

public class ReportProjectFragment extends Fragment implements IReportProjectFragment {


    //                                                                                     CONSTANTS

    static final String ARG_PROJECT_MODEL = "project_model";


    //                                                                                  CONSTRUCTORS

    public ReportProjectFragment() {
    }


    //                                                                                       FACTORY

    public static ReportProjectFragment newInstance(ProjectModel projectModel) {
        ReportProjectFragment fragment = new ReportProjectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        fragment.setArguments(args);
        return fragment;
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_report_project, container, false);
        // TODO implement
        return rootView;
    }

}

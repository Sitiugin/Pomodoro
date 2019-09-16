package com.glebworx.pomodoro.ui.main.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;

import butterknife.ButterKnife;


public class ReportFragment extends Fragment {

    public ReportFragment() { }

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;

    }

}

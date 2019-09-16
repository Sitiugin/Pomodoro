package com.glebworx.pomodoro.ui.main.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;

import butterknife.ButterKnife;


public class AddTaskFragment extends Fragment {


    public AddTaskFragment() { }

    public static AddTaskFragment newInstance() {
        return new AddTaskFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

}

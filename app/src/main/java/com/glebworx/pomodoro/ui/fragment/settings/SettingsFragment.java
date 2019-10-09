package com.glebworx.pomodoro.ui.fragment.settings;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.settings.interfaces.ISettingsFragment;


public class SettingsFragment extends Fragment implements ISettingsFragment {


    public SettingsFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        return rootView;

    }

}

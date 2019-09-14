package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddProjectFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.button_close) AppCompatImageButton closeButton;


    //                                                                                    ATTRIBUTES

    private OnAddProjectFragmentInteractionListener listener;


    //                                                                                     LIFECYCLE

    public AddProjectFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_project, container, false);
        ButterKnife.bind(this, rootView);

        initClickEvents();

        return rootView;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnAddProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }



    private void initClickEvents() {
        closeButton.setOnClickListener(view -> listener.onCloseClicked());
    }

    public interface OnAddProjectFragmentInteractionListener {
        void onCloseClicked();
    }

}

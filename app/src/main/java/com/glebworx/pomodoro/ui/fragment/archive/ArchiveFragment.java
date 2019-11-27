package com.glebworx.pomodoro.ui.fragment.archive;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.item.ProjectItem;
import com.mikepenz.fastadapter.IItemAdapter;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ArchiveFragment extends Fragment implements IArchiveFragment {


    //                                                                                    ATTRIBUTES

    private Context context;
    private IArchiveFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ArchiveFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public ArchiveFragment() {
    }

    public static ArchiveFragment newInstance() {
        return new ArchiveFragment();
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_archive, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ArchiveFragmentPresenter(this, fragmentListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (IArchiveFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                     INTERFACE

    @Override
    public void onInitView(IItemAdapter.Predicate<ProjectItem> predicate) {

    }

    @Override
    public void onItemAdded(ProjectItem item) {

    }

    @Override
    public void onItemModified(ProjectItem item) {

    }

    @Override
    public void onItemDeleted(ProjectItem item) {

    }
}

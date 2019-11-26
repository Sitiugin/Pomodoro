package com.glebworx.pomodoro.ui.fragment.archive;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentPresenter;

class ArchiveFragmentPresenter implements IArchiveFragmentPresenter {

    private @NonNull
    IArchiveFragment presenterListener;
    private @NonNull
    IArchiveFragmentInteractionListener interactionListener;

    ArchiveFragmentPresenter(@NonNull IArchiveFragment presenterListener,
                             @NonNull IArchiveFragmentInteractionListener interactionListener) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init();
    }

    @Override
    public void init() {
        // TODO implement
    }

}

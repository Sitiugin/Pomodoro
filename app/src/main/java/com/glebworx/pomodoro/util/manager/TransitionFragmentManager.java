package com.glebworx.pomodoro.util.manager;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TransitionFragmentManager {


    /*
                                                                                        CONSTANTS */
    private static final int DELAY = 300;


    /*                                                                                 PROPERTIES */

    private Handler handler;
    private FragmentManager fragmentManager;
    private String activeFragmentTag;
    private int containerId;

    public TransitionFragmentManager(FragmentManager fragmentManager,
                                     int containerId) {

        this.handler = new Handler(Looper.getMainLooper());
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;

    }

    public void addRootFragment(Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(containerId, fragment)
                .commitAllowingStateLoss();
        /*handler.post(() -> {
            fragmentManager.executePendingTransactions();
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(containerId,
                            fragment,
                            fragment.getClass().getSimpleName())
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        });*/
    }

}

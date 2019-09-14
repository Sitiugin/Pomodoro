package com.glebworx.pomodoro.util.manager;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
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

    public void pushToBackStack(@NonNull Fragment fragment) {
        //fragment.setEnterTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //fragment.setExitTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentManager.executePendingTransactions();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                /*.setCustomAnimations(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)*/
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void pushToBackStack(@NonNull Fragment fragment,
                                @NonNull String sharedElementKey,
                                @NonNull View sharedElementView) {
        fragmentManager.executePendingTransactions();
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addSharedElement(sharedElementView, sharedElementKey)
                .replace(containerId, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void pushToBackStack(@NonNull Fragment fragment, @NonNull Map<String, View> sharedElementsMap) {
        fragmentManager.executePendingTransactions();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Set<String> keys = sharedElementsMap.keySet();
        for (String key: keys) {
            transaction.addSharedElement(sharedElementsMap.get(key), key);
        }
        transaction.replace(containerId, fragment).addToBackStack(null).commitAllowingStateLoss();
    }

    public void popFromBackStack() {
        fragmentManager.executePendingTransactions();
        fragmentManager.popBackStack();
    }

}

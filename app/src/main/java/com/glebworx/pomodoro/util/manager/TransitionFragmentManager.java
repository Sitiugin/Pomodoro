package com.glebworx.pomodoro.util.manager;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class TransitionFragmentManager {


    /*
                                                                                        CONSTANTS */
    private static final int DELAY = 300;


    /*                                                                                 PROPERTIES */

    private Handler handler;
    private FragmentManager fragmentManager;
    private String activeFragmentTag;
    private int containerId;
    private Stack<Fragment> backStack;

    public TransitionFragmentManager(FragmentManager fragmentManager,
                                     int containerId) {

        this.handler = new Handler(Looper.getMainLooper());
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.backStack = new Stack<>();

    }

    public void addRootFragment(Fragment fragment) {
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(containerId, backStack.push(fragment))
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
        fragmentManager.executePendingTransactions();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .hide(backStack.peek())
                .add(containerId, backStack.push(fragment))
                //.addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void pushToBackStack(@NonNull Fragment fragment,
                                @NonNull View sharedElementView,
                                @NonNull String sharedElementKey) {
        fragmentManager.executePendingTransactions();
        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addSharedElement(sharedElementView, sharedElementKey)
                .hide(backStack.peek())
                .add(containerId, backStack.push(fragment))
                //.addToBackStack(null)
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
        transaction
                .hide(backStack.peek())
                .add(containerId, backStack.push(fragment))
                .commitAllowingStateLoss();
    }

    public boolean hasStackedFragments() {
        return backStack.size() > 1;
    }

    public void popFromBackStack() {
        if (backStack.size() < 2) {
            return;
        }
        fragmentManager.executePendingTransactions();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .remove(backStack.pop())
                .show(backStack.peek())
                .commitAllowingStateLoss();
    }

    public void clearAllFragments() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentManager.getFragments()) {
            transaction.remove(fragment);
        }
        backStack.clear();
        transaction.commitNowAllowingStateLoss();
    }

}

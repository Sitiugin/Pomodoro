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

public class NavigationFragmentManager {


    /*
                                                                                        CONSTANTS */
    private static final int DELAY = 300;


    /*                                                                                 PROPERTIES */

    private Handler handler;
    private FragmentManager fragmentManager;
    private String activeFragmentTag;
    private String[] tags;
    private int containerId;

    public NavigationFragmentManager(FragmentManager fragmentManager,
                                     int containerId,
                                     Map<String, Fragment> fragmentMap) {

        this.handler = new Handler(Looper.getMainLooper());
        this.fragmentManager = fragmentManager;
        this.tags = new String[fragmentMap.size()];
        this.containerId = containerId;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Iterator iterator = fragmentMap.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {

            Map.Entry pair = (Map.Entry) iterator.next();
            String tag = (String) pair.getKey();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                fragment = (Fragment) pair.getValue();
                fragmentTransaction.add(containerId, fragment, tag);
            }
            fragmentTransaction.hide(fragment);
            tags[i] = tag;
            i++;
            iterator.remove(); // avoids a ConcurrentModificationException

        }

        fragmentTransaction.commitNowAllowingStateLoss();

    }

    public void selectFragment(String activeFragmentTag) {
        for (String tag: tags) {
            if (tag.equals(activeFragmentTag)) {
                showFragmentDelayed(tag);
                this.activeFragmentTag = activeFragmentTag;
            } else {
                hideFragment(tag);
            }
        }
    }

    public String getSelectedFragmentTag() {
        return this.activeFragmentTag;
    }

    private void hideFragment(String tag) {

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            return;
        }
        if (fragment.isVisible()) {
            handler.post(() -> {
                fragmentManager.executePendingTransactions();
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .hide(fragment)
                        .commitAllowingStateLoss();
            });
        }

    }

    private void showFragmentDelayed(String tag) {

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            return;
        }

        if (!fragment.isVisible()) {
            handler.postDelayed(() -> {

                fragmentManager.executePendingTransactions();
                try {
                    fragmentManager
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .show(fragment)
                            .commitAllowingStateLoss();
                } catch (Exception e) { }

            }, DELAY);
        }

    }

}

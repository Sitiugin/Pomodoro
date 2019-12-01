package com.glebworx.pomodoro.util.manager;

import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class UndoManager<Item extends IItem> {

    private static final int ACTION_REMOVE = 2;

    private FastAdapter<Item> adapter;
    private UndoListener<Item> undoListener;
    private History history = null;
    private Snackbar snackBar;
    private boolean committed;

    private Snackbar.Callback snackBarCallback = new Snackbar.Callback() {
        @Override
        public void onShown(Snackbar sb) {
            super.onShown(sb);
            committed = false;
        }

        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            super.onDismissed(transientBottomBar, event);
            if ((event == Snackbar.Callback.DISMISS_EVENT_ACTION) || committed) {
                return;
            }
            notifyCommit();
        }
    };

    public UndoManager(FastAdapter<Item> adapter,
                       View view,
                       String snackBarText,
                       String actionText,
                       UndoListener<Item> undoListener) {
        this.adapter = adapter;
        this.undoListener = undoListener;
        snackBar = Snackbar.make(view, snackBarText, Snackbar.LENGTH_LONG);
        View snackBarView = snackBar.getView();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackBarView.getLayoutParams();
        //params.bottomMargin = 300;
        //snackBarView.setBottom(300)
        params.bottomMargin = 500;
        //snackBarView.setElevation(30);
        //snackBarView.
        //snackBarView.offsetTopAndBottom(300);
        /*params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + 200);*/
        //snackBarView.setPadding(0, 0, 0, 300);
        //params.dodgeInsetEdges = Gravity.TOP;
        //params.dodgeInsetEdges = Gravity.TOP;
        //snackBarView.setLayoutParams(params);
        //snackBar.show();

        snackBar.addCallback(snackBarCallback).setAction(actionText, v -> undoChange());
    }

    public void remove(int position) {

        if (history != null) {
            committed = true;
            notifyCommit();
        }

        History history = new History();
        history.action = ACTION_REMOVE;
        history.items.add(adapter.getRelativeInfo(position));
        Collections.sort(history.items, (lhs, rhs) -> Integer.compare(lhs.position, rhs.position));

        this.history = history;
        doChange();

        snackBar.show();

    }

    public Snackbar getSnackBar() {
        return snackBar;
    }


    private void notifyCommit() {

        if (history == null) {
            return;
        }

        if (history.action == ACTION_REMOVE) {

            SortedSet<Integer> positions = new TreeSet<>(Integer::compareTo);
            for (FastAdapter.RelativeInfo<Item> relativeInfo : history.items) {
                positions.add(relativeInfo.position);
            }

            undoListener.commitRemove(positions, history.items);
            history = null;

        }

    }

    private void doChange() {

        if (history == null) {
            return;
        }

        if (history.action == ACTION_REMOVE) {
            for (int i = history.items.size() - 1; i >= 0; i--) {
                FastAdapter.RelativeInfo<Item> relativeInfo = history.items.get(i);
                if (relativeInfo.adapter instanceof IItemAdapter) {
                    ((IItemAdapter) relativeInfo.adapter).remove(relativeInfo.position);
                }
            }
        }

    }

    private void undoChange() {

        if (history == null) {
            return;
        }

        if (history.action == ACTION_REMOVE) {
            for (int i = 0, size = history.items.size(); i < size; i++) {
                FastAdapter.RelativeInfo<Item> relativeInfo = history.items.get(i);
                if (relativeInfo.adapter instanceof IItemAdapter) {
                    IItemAdapter<?, Item> adapter = (IItemAdapter<?, Item>) relativeInfo.adapter;
                    adapter.addInternal(relativeInfo.position, Collections.singletonList(relativeInfo.item));
                    if (relativeInfo.item.isSelected()) {
                        this.adapter.select(relativeInfo.position);
                    }
                }
            }
        }

        history = null;

    }

    public interface UndoListener<Item extends IItem> {
        void commitRemove(Set<Integer> positions, ArrayList<FastAdapter.RelativeInfo<Item>> removed);
    }

    private class History {
        public int action;
        public ArrayList<FastAdapter.RelativeInfo<Item>> items = new ArrayList<>();
    }

}

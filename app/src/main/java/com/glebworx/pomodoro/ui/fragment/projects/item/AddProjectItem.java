package com.glebworx.pomodoro.ui.fragment.projects.item;

import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class AddProjectItem
        extends AbstractItem<AddProjectItem, AddProjectItem.ViewHolder>
        implements ISwipeable<AddProjectItem, AddProjectItem> {


    //                                                                                  CONSTRUCTORS

    public AddProjectItem() {
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public AddProjectItem.ViewHolder getViewHolder(@NonNull View view) {
        return new AddProjectItem.ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_add_project;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_add_project;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }


    //                                                                                       HELPERS

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public AddProjectItem withIsSwipeable(boolean swipeable) {
        return this;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<AddProjectItem> {

        ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(@NonNull AddProjectItem item, @NonNull List<Object> payloads) {
        }

        @Override
        public void unbindView(@NonNull AddProjectItem item) {
        }

    }

}

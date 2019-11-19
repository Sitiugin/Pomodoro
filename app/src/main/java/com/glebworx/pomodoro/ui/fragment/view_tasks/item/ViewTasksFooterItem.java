package com.glebworx.pomodoro.ui.fragment.view_tasks.item;

import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ViewTasksFooterItem extends AbstractItem<ViewTasksFooterItem, ViewTasksFooterItem.ViewHolder> {

    public ViewTasksFooterItem() {
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_view_tasks_footer;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_view_tasks_footer;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewTasksFooterItem> {

        ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(@NonNull ViewTasksFooterItem item, @NonNull List<Object> payloads) {
        }

        @Override
        public void unbindView(@NonNull ViewTasksFooterItem item) {
        }

    }
}

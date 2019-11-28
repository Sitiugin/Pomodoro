package com.glebworx.pomodoro.ui.fragment.archive.item;

import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;
import java.util.Objects;

public class ArchivedProjectItem
        extends AbstractItem<ArchivedProjectItem, ArchivedProjectItem.ViewHolder>
        implements ISwipeable<ArchivedProjectItem, ArchivedProjectItem> {

    private ProjectModel model;

    public ArchivedProjectItem(@NonNull ProjectModel model) {
        this.model = model;
    }

    @NonNull
    @Override
    public ArchivedProjectItem.ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_archived_project;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_archived_project;
    }

    @Override
    public boolean isSwipeable() {
        return true;
    }

    @Override
    public ArchivedProjectItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<ArchivedProjectItem> {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(@NonNull ArchivedProjectItem item, @NonNull List<Object> payloads) {

        }

        @Override
        public void unbindView(@NonNull ArchivedProjectItem item) {

        }
    }

}

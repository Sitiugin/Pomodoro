package com.glebworx.pomodoro.ui.fragment.archive.item;

import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class ArchiveHeaderItem
        extends AbstractItem<ArchiveHeaderItem, ArchiveHeaderItem.ViewHolder>
        implements ISwipeable<ArchiveHeaderItem, ArchiveHeaderItem> {

    public ArchiveHeaderItem() {

    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_archive_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_archive_header;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public ArchiveHeaderItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<ArchiveHeaderItem> {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindView(@NonNull ArchiveHeaderItem item, @NonNull List<Object> payloads) {

        }

        @Override
        public void unbindView(@NonNull ArchiveHeaderItem item) {

        }

    }

}

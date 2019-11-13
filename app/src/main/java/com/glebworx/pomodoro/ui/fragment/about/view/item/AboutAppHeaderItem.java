package com.glebworx.pomodoro.ui.fragment.about.view.item;

import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class AboutAppHeaderItem extends AbstractItem<AboutAppHeaderItem, AboutAppHeaderItem.ViewHolder> {

    public AboutAppHeaderItem() {
    }

    @NonNull
    @Override
    public AboutAppHeaderItem.ViewHolder getViewHolder(@NonNull View view) {
        return new AboutAppHeaderItem.ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_about_app_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_about_app_header;
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<AboutAppHeaderItem> {

        ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(@NonNull AboutAppHeaderItem item, @NonNull List<Object> payloads) {
        }

        @Override
        public void unbindView(@NonNull AboutAppHeaderItem item) {
        }
    }
}

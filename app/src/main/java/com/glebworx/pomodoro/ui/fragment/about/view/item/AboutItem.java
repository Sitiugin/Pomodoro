package com.glebworx.pomodoro.ui.fragment.about.view.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class AboutItem extends AbstractItem<AboutItem, AboutItem.ViewHolder> {

    private String title;
    private String description;

    public AboutItem(String description) {
        this.title = null;
        this.description = description;
    }

    public AboutItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_about;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_about;
    }

    private String getTitle() {
        return title;
    }

    private String getDescription() {
        return description;
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<AboutItem> {

        private AppCompatTextView titleTextView;
        private AppCompatTextView descriptionTextView;

        ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.text_view_title);
            descriptionTextView = view.findViewById(R.id.text_view_description);
        }

        @Override
        public void bindView(@NonNull AboutItem item, @NonNull List<Object> payloads) {
            String title = item.getTitle();
            titleTextView.setText(title);
            titleTextView.setVisibility(title == null ? View.GONE : View.VISIBLE);
            descriptionTextView.setText(item.getDescription());
        }

        @Override
        public void unbindView(@NonNull AboutItem item) {
            titleTextView.setText(null);
            descriptionTextView.setText(null);
        }
    }

}

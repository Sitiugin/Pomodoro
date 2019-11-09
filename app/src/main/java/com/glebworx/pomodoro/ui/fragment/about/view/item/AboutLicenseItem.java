package com.glebworx.pomodoro.ui.fragment.about.view.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class AboutLicenseItem extends AbstractItem<AboutLicenseItem, AboutLicenseItem.ViewHolder> {

    private String title;
    private String description;
    private String libraries;

    public AboutLicenseItem(String title, String description, String libraries) {
        this.title = title;
        this.description = description;
        this.libraries = libraries;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_about_license;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_about_license;
    }

    private String getTitle() {
        return title;
    }

    private String getDescription() {
        return description;
    }

    private String getLibraries() {
        return libraries;
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<AboutLicenseItem> {

        private AppCompatTextView titleTextView;
        private AppCompatTextView descriptionTextView;
        private AppCompatTextView librariesTextView;

        ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.text_view_title);
            descriptionTextView = view.findViewById(R.id.text_view_description);
            librariesTextView = view.findViewById(R.id.text_view_libraries);
        }

        @Override
        public void bindView(@NonNull AboutLicenseItem item, @NonNull List<Object> payloads) {
            String title = item.getTitle();
            titleTextView.setText(title);
            descriptionTextView.setText(item.getDescription());
            librariesTextView.setText(item.getLibraries());
        }

        @Override
        public void unbindView(@NonNull AboutLicenseItem item) {
            titleTextView.setText(null);
            descriptionTextView.setText(null);
            librariesTextView.setText(null);
        }
    }

}

package com.glebworx.pomodoro.item;


import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.constants.Constants;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.triggertrap.seekarc.SeekArc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

public class ProjectItem extends AbstractItem<ProjectItem, ProjectItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE_TIME, Locale.getDefault());

    private ProjectModel model;


    //                                                                                  CONSTRUCTORS

    public ProjectItem(@NonNull ProjectModel model) {
        this.model = model;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_project;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_project;
    }


    //                                                                                       HELPERS

    public @Nonnull ProjectModel getModel() {
        return this.model;
    }

    public @NonNull String getProjectName() {
        return this.model.getName();
    }

    public @Nullable String getDueDate(Context context) {
        if (this.model.getDueDate() == null) {
            return null;
        }
        Date dueDate = this.model.getDueDate();
        return context.getString(R.string.core_due, dateFormat.format(dueDate));
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ProjectItem> {

        private Context context;
        private AppCompatTextView titleTextView;
        private AppCompatTextView dueDateTextView;
        private SeekArc progressSeekArc;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            titleTextView = view.findViewById(R.id.text_view_title);
            dueDateTextView = view.findViewById(R.id.text_view_due_date);
            progressSeekArc = view.findViewById(R.id.seek_arc_progress);
        }

        @Override
        public void bindView(@NonNull ProjectItem item, @NonNull List<Object> payloads) {
            titleTextView.setText(item.getProjectName());
            dueDateTextView.setText(item.getDueDate(context));
        }

        @Override
        public void unbindView(@NonNull ProjectItem item) {
            titleTextView.setText(null);
            dueDateTextView.setText(null);
        }

    }

}
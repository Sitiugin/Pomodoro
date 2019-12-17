package com.glebworx.pomodoro.ui.item;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.triggertrap.seekarc.SeekArc;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;


public class ProjectItem
        extends AbstractItem<ProjectItem, ProjectItem.ViewHolder>
        implements ISwipeable<ProjectItem, ProjectItem> {


    //                                                                                    ATTRIBUTES

    private static NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale.getDefault());
    private static Date currentDate = new Date();

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

    @Override
    public boolean isSwipeable() {
        return true;
    }

    @Override
    public ProjectItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectItem)) return false;
        ProjectItem that = (ProjectItem) o;
        return model.equals(that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }


    //                                                                                       HELPERS

    public @Nonnull ProjectModel getModel() {
        return this.model;
    }

    public @NonNull String getProjectName() {
        return model.getName();
    }

    public @Nullable String getColorTag() {
        return model.getColorTag();
    }

    private @Nullable
    String getDueDateString(Context context) {
        if (model.getDueDate() == null) {
            return null;
        }
        return DateTimeManager.getDueDateString(context, model.getDueDate(), currentDate);
    }

    private float getProgress() {
        return model.getProgress();
    }

    private @NonNull
    String getProgressString(double progress) {
        return numberFormat.format(progress);
    }

    private boolean isOverdue() {
        return model.getDueDate().compareTo(new Date()) < 0;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ProjectItem> {

        private Context context;
        private Drawable colorTagDrawable;
        private AppCompatTextView titleTextView;
        private AppCompatTextView dueDateTextView;
        private SeekArc progressSeekArc;
        private AppCompatTextView progressTextView;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            colorTagDrawable = ((LayerDrawable) ((AppCompatImageView) view.findViewById(R.id.view_color_tag)).getDrawable())
                    .findDrawableByLayerId(R.id.shape_color_tag);
            titleTextView = view.findViewById(R.id.text_view_title);
            dueDateTextView = view.findViewById(R.id.text_view_due_date);
            progressSeekArc = view.findViewById(R.id.seek_arc_progress);
            progressTextView = view.findViewById(R.id.text_view_progress);
        }

        @Override
        public void bindView(@NonNull ProjectItem item, @NonNull List<Object> payloads) {
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
            titleTextView.setText(item.getProjectName());
            dueDateTextView.setText(item.getDueDateString(context));
            if (item.isOverdue()) {
                dueDateTextView.setTextColor(context.getColor(R.color.colorError));
            } else {
                dueDateTextView.setTextColor(context.getColor(android.R.color.darker_gray));
            }
            float progress = item.getProgress();
            if (progress > 1) {
                progress = 1;
            }
            int arcProgress = Math.round(progress * 100);
            progressSeekArc.setProgress(arcProgress);
            progressTextView.setText(item.getProgressString(progress));
        }

        @Override
        public void unbindView(@NonNull ProjectItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            titleTextView.setText(null);
            dueDateTextView.setText(null);
            dueDateTextView.setTextColor(context.getColor(android.R.color.darker_gray));
            progressSeekArc.setProgress(0);
            progressTextView.setText(null);
        }

    }

}
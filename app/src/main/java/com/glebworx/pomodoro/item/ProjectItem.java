package com.glebworx.pomodoro.item;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.constants.Constants;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.triggertrap.seekarc.SeekArc;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_ALIZARIN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_AMETHYST_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_CARROT_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_EMERALD_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_PETER_RIVER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_SUNFLOWER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_TURQUOISE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_WET_ASPHALT_HEX;

public class ProjectItem extends AbstractItem<ProjectItem, ProjectItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());
    private static NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale.getDefault());

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectItem)) return false;
        if (!super.equals(o)) return false;
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

    public @Nullable String getDueDateString(Context context) {
        if (model.getDueDate() == null) {
            return null;
        }
        Date dueDate = model.getDueDate();
        return context.getString(R.string.core_due, dateFormat.format(dueDate));
    }

    public double getProgressRatio() {
        return model.getProgressRatio();
    }

    public @NonNull String getProgressString(double progress) {
        return numberFormat.format(progress);
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ProjectItem> {

        private Context context;
        private AppCompatTextView titleTextView;
        private AppCompatTextView dueDateTextView;
        private SeekArc progressSeekArc;
        private AppCompatTextView progressTextView;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            titleTextView = view.findViewById(R.id.text_view_title);
            dueDateTextView = view.findViewById(R.id.text_view_due_date);
            progressSeekArc = view.findViewById(R.id.seek_arc_progress);
            progressTextView = view.findViewById(R.id.text_view_progress);
        }

        @Override
        public void bindView(@NonNull ProjectItem item, @NonNull List<Object> payloads) {
            titleTextView.setText(item.getProjectName());
            titleTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    ColorManager.getDrawable(context, item.getColorTag()),
                    null);
            dueDateTextView.setText(item.getDueDateString(context));
            double progressRatio = item.getProgressRatio();
            progressSeekArc.setProgress((int) Math.round(progressRatio * 100));
            progressTextView.setText(item.getProgressString(progressRatio));
        }

        @Override
        public void unbindView(@NonNull ProjectItem item) {
            titleTextView.setText(null);
            titleTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            dueDateTextView.setText(null);
            progressSeekArc.setProgress(0);
            progressTextView.setText(null);
        }

    }

}
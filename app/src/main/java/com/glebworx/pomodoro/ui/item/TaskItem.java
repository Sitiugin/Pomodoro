package com.glebworx.pomodoro.ui.item;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

public class TaskItem
        extends AbstractItem<TaskItem, TaskItem.ViewHolder>
        implements ISwipeable<TaskItem, TaskItem> {


    //                                                                                    ATTRIBUTES

    private static Date currentDate = new Date();

    private ProjectModel projectModel;
    private TaskModel model;


    //                                                                                  CONSTRUCTORS

    public TaskItem(@Nullable ProjectModel projectModel, @NonNull TaskModel model) {
        this.projectModel = projectModel;
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
        return R.id.item_task;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_task;
    }

    @Override
    public boolean isSwipeable() {
        return isEnabled();
    }

    @Override
    public TaskItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }


    //                                                                                       HELPERS

    public @Nonnull TaskModel getModel() {
        return this.model;
    }

    public void setProjectModel(ProjectModel projectModel) {
        this.projectModel = projectModel;
    }

    public @NonNull
    String getProjectName() {
        return this.model.getProjectName();
    }

    public @Nonnull String getTaskName() {
        return this.model.getName();
    }

    private @Nullable
    String getDueDateString(Context context) {
        if (model.getDueDate() == null) {
            return null;
        }
        return DateTimeManager.getDueDateString(context, model.getDueDate(), currentDate);
    }

    private @NonNull
    String getPomodoroRatio(Context context) {
        return context.getString(
                R.string.core_ratio,
                String.valueOf(model.getPomodorosCompleted()),
                String.valueOf(model.getPomodorosAllocated()));
    }

    private boolean isOverdue() {
        return model.getDueDate().compareTo(new Date()) < 0;
    }

    private boolean isOverLimit() {
        return model.isOverLimit();
    }

    public @Nullable
    String getColorTag() {
        if (projectModel == null) {
            return null;
        }
        return projectModel.getColorTag();
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<TaskItem> {

        private Context context;
        private Drawable colorTagDrawable;
        private AppCompatTextView titleTextView;
        private AppCompatTextView dueDateTextView;
        private AppCompatTextView pomodoroTextView;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            colorTagDrawable = ((LayerDrawable) ((AppCompatImageView) view.findViewById(R.id.view_color_tag))
                    .getDrawable())
                    .findDrawableByLayerId(R.id.shape_color_tag);
            titleTextView = view.findViewById(R.id.text_view_title);
            dueDateTextView = view.findViewById(R.id.text_view_due_date);
            pomodoroTextView = view.findViewById(R.id.text_view_pomodoros);
        }

        @Override
        public void bindView(@NonNull TaskItem item, @NonNull List<Object> payloads) {
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
            titleTextView.setText(item.getTaskName());
            dueDateTextView.setText(item.getDueDateString(context));
            pomodoroTextView.setText(item.getPomodoroRatio(context));

            if (item.isOverdue()) {
                dueDateTextView.setTextColor(context.getColor(R.color.colorError));
            } else {
                dueDateTextView.setTextColor(context.getColor(android.R.color.darker_gray));
            }
            if (item.isOverLimit()) {
                pomodoroTextView.setTextColor(context.getColor(R.color.colorError));
                setDrawableColorFilters(R.color.colorError);
            } else {
                pomodoroTextView.setTextColor(context.getColor(android.R.color.darker_gray));
                setDrawableColorFilters(android.R.color.darker_gray);
            }
        }

        @Override
        public void unbindView(@NonNull TaskItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            titleTextView.setText(null);
            dueDateTextView.setText(null);
            dueDateTextView.setTextColor(context.getColor(android.R.color.darker_gray));
            pomodoroTextView.setText(null);
            pomodoroTextView.setTextColor(context.getColor(android.R.color.darker_gray));
            setDrawableColorFilters(android.R.color.darker_gray);
        }

        private void setDrawableColorFilters(int color) {
            for (Drawable drawable : pomodoroTextView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(context.getColor(color), PorterDuff.Mode.SRC_IN));
                }
            }
        }

    }

}

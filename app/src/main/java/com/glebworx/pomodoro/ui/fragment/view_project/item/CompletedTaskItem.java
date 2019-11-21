package com.glebworx.pomodoro.ui.fragment.view_project.item;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

public class CompletedTaskItem extends AbstractItem<CompletedTaskItem, CompletedTaskItem.ViewHolder> implements ISwipeable<CompletedTaskItem, CompletedTaskItem> {


    //                                                                                    ATTRIBUTES

    private static Date currentDate = new Date();

    private TaskModel model;


    //                                                                                  CONSTRUCTORS

    public CompletedTaskItem(@NonNull TaskModel model) {
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
        return R.id.item_task_completed;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_task_completed;
    }


    //                                                                                       HELPERS

    public @Nonnull
    TaskModel getModel() {
        return this.model;
    }

    public @Nonnull
    String getTaskName() {
        return this.model.getName();
    }

    public @Nullable
    String getDueDateString(Context context) {
        if (model.getDueDate() == null) {
            return null;
        }
        return DateTimeManager.getDueDateString(context, model.getDueDate(), currentDate);
    }

    public @NonNull
    String getPomodoroRatio(Context context) {
        return context.getString(
                R.string.core_ratio,
                String.valueOf(model.getPomodorosCompleted()),
                String.valueOf(model.getPomodorosAllocated()));
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public CompletedTaskItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    public String getProjectName() {
        return model.getProjectName();
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<CompletedTaskItem> {

        private Context context;
        private AppCompatTextView titleTextView;
        private AppCompatTextView dueDateTextView;
        private AppCompatTextView pomodoroTextView;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            titleTextView = view.findViewById(R.id.text_view_title);
            dueDateTextView = view.findViewById(R.id.text_view_due_date);
            pomodoroTextView = view.findViewById(R.id.text_view_pomodoros);
        }

        @Override
        public void bindView(@NonNull CompletedTaskItem item, @NonNull List<Object> payloads) {
            titleTextView.setText(item.getTaskName());
            dueDateTextView.setText(item.getDueDateString(context));
            pomodoroTextView.setText(item.getPomodoroRatio(context));
        }

        @Override
        public void unbindView(@NonNull CompletedTaskItem item) {
            titleTextView.setText(null);
            dueDateTextView.setText(null);
            dueDateTextView.setTextColor(context.getColor(android.R.color.darker_gray));
            pomodoroTextView.setText(null);
            pomodoroTextView.setTextColor(context.getColor(android.R.color.darker_gray));
        }

    }

}

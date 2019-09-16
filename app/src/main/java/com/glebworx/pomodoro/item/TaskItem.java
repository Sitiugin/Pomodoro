package com.glebworx.pomodoro.item;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.constants.Constants;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

public class TaskItem extends AbstractItem<TaskItem, TaskItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());
    private TaskModel model;


    //                                                                                  CONSTRUCTORS

    public TaskItem(@NonNull TaskModel model) {
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


    //                                                                                       HELPERS

    public @Nonnull TaskModel getModel() {
        return this.model;
    }

    public @Nonnull String getTaskName() {
        return this.model.getName();
    }

    public @Nullable
    String getDueDateString(Context context) {
        if (model.getDueDate() == null) {
            return null;
        }
        Date dueDate = model.getDueDate();
        return context.getString(R.string.core_due, dateFormat.format(dueDate));
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<TaskItem> {

        private Context context;
        private AppCompatTextView titleTextView;
        private AppCompatTextView dueDateTextView;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            titleTextView = view.findViewById(R.id.text_view_title);
            dueDateTextView = view.findViewById(R.id.text_view_due_date);
        }

        @Override
        public void bindView(@NonNull TaskItem item, @NonNull List<Object> payloads) {
            titleTextView.setText(item.getTaskName());
            dueDateTextView.setText(item.getDueDateString(context));
        }

        @Override
        public void unbindView(@NonNull TaskItem item) {
            titleTextView.setText(null);
            dueDateTextView.setText(null);
        }

    }

}

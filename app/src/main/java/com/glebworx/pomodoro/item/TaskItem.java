package com.glebworx.pomodoro.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.TaskModel;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import javax.annotation.Nonnull;

public class TaskItem extends AbstractItem<TaskItem, TaskItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

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


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<TaskItem> {

        private AppCompatImageView dotImageView;
        private AppCompatTextView titleTextView;
        private AppCompatImageButton optionsButton;

        ViewHolder(View view) {
            super(view);
            dotImageView = view.findViewById(R.id.image_view_dot);
            titleTextView = view.findViewById(R.id.text_view_title);
            optionsButton = view.findViewById(R.id.button_options);
        }

        @Override
        public void bindView(@NonNull TaskItem item, @NonNull List<Object> payloads) {

            titleTextView.setText(item.getTaskName());
        }

        @Override
        public void unbindView(@NonNull TaskItem item) {
            titleTextView.setText(null);
        }

    }

}

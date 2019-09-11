package com.glebworx.pomodoro.item;

import android.view.View;

import androidx.annotation.NonNull;

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

    public TaskItem(@NonNull TaskModel model) { // TODO super?
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


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<TaskItem> {

        ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(@NonNull TaskItem item, @NonNull List<Object> payloads) {

        }

        @Override
        public void unbindView(@NonNull TaskItem item) {

        }

    }

}

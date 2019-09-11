package com.glebworx.pomodoro.item;


import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import javax.annotation.Nonnull;

public class ProjectItem extends AbstractItem<ProjectItem, ProjectItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

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


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ProjectItem> {

        ViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindView(@NonNull ProjectItem item, @NonNull List<Object> payloads) {

        }

        @Override
        public void unbindView(@NonNull ProjectItem item) {

        }

    }

}
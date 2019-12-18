package com.glebworx.pomodoro.ui.fragment.view_project.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class AddTaskItem
        extends AbstractItem<AddTaskItem, AddTaskItem.ViewHolder>
        implements ISwipeable<AddTaskItem, AddTaskItem> {


    //                                                                                    ATTRIBUTES

    private ProjectModel model;


    //                                                                                  CONSTRUCTORS

    public AddTaskItem(ProjectModel model) {
        this.model = model;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public AddTaskItem.ViewHolder getViewHolder(@NonNull View view) {
        return new AddTaskItem.ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_add_task;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_add_task;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public AddTaskItem withIsSwipeable(boolean swipeable) {
        return this;
    }


    //                                                                                       HELPERS

    public @Nullable
    String getColorTag() {
        return model.getColorTag();
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<AddTaskItem> {

        private Context context;
        private Drawable colorTagDrawable;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            colorTagDrawable = ((LayerDrawable) ((AppCompatImageView) view.findViewById(R.id.view_color_tag))
                    .getDrawable())
                    .findDrawableByLayerId(R.id.shape_color_tag);
        }

        @Override
        public void bindView(@NonNull AddTaskItem item, @NonNull List<Object> payloads) {
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
        }

        @Override
        public void unbindView(@NonNull AddTaskItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
        }

    }

}

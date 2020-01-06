package com.glebworx.pomodoro.ui.fragment.view_tasks.item;

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

import java.util.List;

public class ViewTasksFooterItem extends AbstractItem<ViewTasksFooterItem, ViewTasksFooterItem.ViewHolder> {

    private ProjectModel model;

    public ViewTasksFooterItem() {
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_view_tasks_footer;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_view_tasks_footer;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    public boolean hasModel() {
        return model != null;
    }

    public void setModel(ProjectModel model) {
        this.model = model;
    }

    public @Nullable
    String getColorTag() {
        return model == null ? null : model.getColorTag();
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewTasksFooterItem> {

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
        public void bindView(@NonNull ViewTasksFooterItem item, @NonNull List<Object> payloads) {
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
        }

        @Override
        public void unbindView(@NonNull ViewTasksFooterItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
        }

    }

}

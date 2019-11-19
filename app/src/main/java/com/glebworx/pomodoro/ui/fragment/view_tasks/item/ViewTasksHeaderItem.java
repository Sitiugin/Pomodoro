package com.glebworx.pomodoro.ui.fragment.view_tasks.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ViewTasksHeaderItem extends AbstractItem<ViewTasksHeaderItem, ViewTasksHeaderItem.ViewHolder> {

    private ProjectModel model;

    public ViewTasksHeaderItem(ProjectModel model) {
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
        return R.id.item_view_tasks_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_view_tasks_header;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }


    //                                                                                       HELPERS

    public String getTitle() {
        return this.model.getName();
    }

    public String getColorTag() {
        return this.model.getColorTag();
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewTasksHeaderItem> {

        private Context context;
        private AppCompatTextView titleTextView;
        private Drawable colorTagDrawable;

        ViewHolder(View view) {

            super(view);

            context = view.getContext();

            titleTextView = view.findViewById(R.id.text_view_title);
            colorTagDrawable = ((LayerDrawable) view.findViewById(R.id.view_color_tag).getBackground())
                    .findDrawableByLayerId(R.id.shape_color_tag);

        }

        @Override
        public void bindView(@NonNull ViewTasksHeaderItem item, @NonNull List<Object> payloads) {
            titleTextView.setText(item.getTitle());
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
        }

        @Override
        public void unbindView(@NonNull ViewTasksHeaderItem item) {
            titleTextView.setText(null);
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
        }

    }
}

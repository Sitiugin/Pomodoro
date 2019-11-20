package com.glebworx.pomodoro.ui.fragment.view_tasks.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ViewTasksHeaderItem extends AbstractItem<ViewTasksHeaderItem, ViewTasksHeaderItem.ViewHolder> {

    private String projectName;
    private String colorTag;

    public ViewTasksHeaderItem(String projectName) {
        this.projectName = projectName;
        this.colorTag = null;
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
        return this.projectName;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewTasksHeaderItem> {

        private Context context;
        private AppCompatTextView titleTextView;
        private Drawable colorTagDrawable;

        ViewHolder(View view) {
            super(view);
            context = view.getContext();
            colorTagDrawable = ((LayerDrawable) view.findViewById(R.id.view_color_tag).getBackground())
                    .findDrawableByLayerId(R.id.shape_color_tag);
            titleTextView = view.findViewById(R.id.text_view_title);

        }

        @Override
        public void bindView(@NonNull ViewTasksHeaderItem item, @NonNull List<Object> payloads) {
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
            titleTextView.setText(item.getTitle());
        }

        @Override
        public void unbindView(@NonNull ViewTasksHeaderItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            titleTextView.setText(null);
        }

    }

}

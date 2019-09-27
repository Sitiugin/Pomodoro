package com.glebworx.pomodoro.item;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class ViewProjectHeaderItem extends AbstractItem<ViewProjectHeaderItem, ViewProjectHeaderItem.ViewHolder> implements ISwipeable<ViewProjectHeaderItem, ViewProjectHeaderItem> {


    //                                                                                    ATTRIBUTES

    private String colorTag;
    private View.OnClickListener onClickListener;


    //                                                                                  CONSTRUCTORS

    public ViewProjectHeaderItem(String colorTag, View.OnClickListener onClickListener) {
        this.colorTag = colorTag;
        this.onClickListener = onClickListener;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_view_project_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_view_project_header;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public ViewProjectHeaderItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    public String getColorTag() {
        return this.colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }

    //                                                                                       HELPERS

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewProjectHeaderItem> {

        private Context context;
        private AppCompatImageButton optionsButton;
        private Drawable colorTagDrawable;

        ViewHolder(View view) {
            super(view);
            context = view.getContext();
            optionsButton = view.findViewById(R.id.button_options);
            colorTagDrawable = ((LayerDrawable) view.findViewById(R.id.view_color_tag).getBackground())
                    .findDrawableByLayerId(R.id.shape_color_tag);

        }

        @Override
        public void bindView(@NonNull ViewProjectHeaderItem item, @NonNull List<Object> payloads) {
            optionsButton.setOnClickListener(item.getOnClickListener());
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
        }

        @Override
        public void unbindView(@NonNull ViewProjectHeaderItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            optionsButton.setOnClickListener(null);
        }

    }

}
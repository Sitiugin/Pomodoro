package com.glebworx.pomodoro.item;


import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class ViewProjectDecorativeHeaderItem extends AbstractItem<ViewProjectDecorativeHeaderItem, ViewProjectDecorativeHeaderItem.ViewHolder> implements ISwipeable<ViewProjectDecorativeHeaderItem, ViewProjectDecorativeHeaderItem> {


    //                                                                                    ATTRIBUTES


    //                                                                                  CONSTRUCTORS

    public ViewProjectDecorativeHeaderItem() {

    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_view_project_header_decorative;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_view_project_header_decorative;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public ViewProjectDecorativeHeaderItem withIsSwipeable(boolean swipeable) {
        return this;
    }


    //                                                                                       HELPERS



    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewProjectDecorativeHeaderItem> {

        private Context context;

        ViewHolder(View view) {
            super(view);
            context = view.getContext();

        }

        @Override
        public void bindView(@NonNull ViewProjectDecorativeHeaderItem item, @NonNull List<Object> payloads) {


        }

        @Override
        public void unbindView(@NonNull ViewProjectDecorativeHeaderItem item) {


        }

    }

}
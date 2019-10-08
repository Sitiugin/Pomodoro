package com.glebworx.pomodoro.ui.main.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class AddItem
        extends AbstractItem<AddItem, AddItem.ViewHolder>
        implements ISwipeable<AddItem, AddItem> {


    //                                                                                    ATTRIBUTES

    private String buttonText;
    private boolean showRoundedBg;


    //                                                                                  CONSTRUCTORS

    public AddItem(String buttonText) {
        this.buttonText = buttonText;
        this.showRoundedBg = false;
    }

    public AddItem(String buttonText, boolean showRoundedBg) {
        this.buttonText = buttonText;
        this.showRoundedBg = showRoundedBg;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public AddItem.ViewHolder getViewHolder(@NonNull View view) {
        return new AddItem.ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_add;
    }

    @Override
    public int getLayoutRes() {
        return showRoundedBg ? R.layout.item_add_bottom_view : R.layout.item_add;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }


    //                                                                                       HELPERS

    public String getButtonText() {
        return buttonText;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public AddItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    public void setShowRoundedBg(boolean showRoundedBg) {
        this.showRoundedBg = showRoundedBg;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<AddItem> {

        private AppCompatButton addButton;

        ViewHolder(View view) {
            super(view);
            addButton = view.findViewById(R.id.item_add);
        }

        @Override
        public void bindView(@NonNull AddItem item, @NonNull List<Object> payloads) {

            addButton.setText(item.getButtonText());
        }

        @Override
        public void unbindView(@NonNull AddItem item) {
            addButton.setText(null);
        }

    }

}

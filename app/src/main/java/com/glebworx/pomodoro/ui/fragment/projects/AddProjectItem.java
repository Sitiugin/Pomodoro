package com.glebworx.pomodoro.ui.fragment.projects;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class AddProjectItem
        extends AbstractItem<AddProjectItem, AddProjectItem.ViewHolder>
        implements ISwipeable<AddProjectItem, AddProjectItem> {


    //                                                                                    ATTRIBUTES

    private String buttonText;


    //                                                                                  CONSTRUCTORS

    public AddProjectItem(String buttonText) {
        this.buttonText = buttonText;
    }

    public AddProjectItem(String buttonText, boolean showRoundedBg) {
        this.buttonText = buttonText;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public AddProjectItem.ViewHolder getViewHolder(@NonNull View view) {
        return new AddProjectItem.ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_add;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_add_project;
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
    public AddProjectItem withIsSwipeable(boolean swipeable) {
        return this;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<AddProjectItem> {

        private AppCompatButton addButton;

        ViewHolder(View view) {
            super(view);
            addButton = view.findViewById(R.id.item_add);
        }

        @Override
        public void bindView(@NonNull AddProjectItem item, @NonNull List<Object> payloads) {

            addButton.setText(item.getButtonText());
        }

        @Override
        public void unbindView(@NonNull AddProjectItem item) {
            addButton.setText(null);
        }

    }

}

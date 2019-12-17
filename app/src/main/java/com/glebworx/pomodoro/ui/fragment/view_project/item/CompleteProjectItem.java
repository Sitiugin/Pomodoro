package com.glebworx.pomodoro.ui.fragment.view_project.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class CompleteProjectItem
        extends AbstractItem<CompleteProjectItem, CompleteProjectItem.ViewHolder>
        implements ISwipeable<CompleteProjectItem, CompleteProjectItem> {


    //                                                                                    ATTRIBUTES

    private String buttonText;


    //                                                                                  CONSTRUCTORS

    public CompleteProjectItem(String buttonText) {
        this.buttonText = buttonText;
    }

    public CompleteProjectItem(String buttonText, boolean showRoundedBg) {
        this.buttonText = buttonText;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public CompleteProjectItem.ViewHolder getViewHolder(@NonNull View view) {
        return new CompleteProjectItem.ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_complete_project;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_complete_project;
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
    public CompleteProjectItem withIsSwipeable(boolean swipeable) {
        return this;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<CompleteProjectItem> {

        private AppCompatButton addButton;

        ViewHolder(View view) {
            super(view);
            addButton = view.findViewById(R.id.item_complete_project);
        }

        @Override
        public void bindView(@NonNull CompleteProjectItem item, @NonNull List<Object> payloads) {

            addButton.setText(item.getButtonText());
        }

        @Override
        public void unbindView(@NonNull CompleteProjectItem item) {
            addButton.setText(null);
        }

    }

}

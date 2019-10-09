package com.glebworx.pomodoro.ui.fragment.view_project;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.util.List;

public class AddTaskItem
        extends AbstractItem<AddTaskItem, AddTaskItem.ViewHolder>
        implements ISwipeable<AddTaskItem, AddTaskItem> {


    //                                                                                    ATTRIBUTES

    private String buttonText;


    //                                                                                  CONSTRUCTORS

    public AddTaskItem(String buttonText) {
        this.buttonText = buttonText;
    }

    public AddTaskItem(String buttonText, boolean showRoundedBg) {
        this.buttonText = buttonText;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public AddTaskItem.ViewHolder getViewHolder(@NonNull View view) {
        return new AddTaskItem.ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_add;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_add_task;
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
    public AddTaskItem withIsSwipeable(boolean swipeable) {
        return this;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<AddTaskItem> {

        private AppCompatButton addButton;

        ViewHolder(View view) {
            super(view);
            addButton = view.findViewById(R.id.item_add);
        }

        @Override
        public void bindView(@NonNull AddTaskItem item, @NonNull List<Object> payloads) {

            addButton.setText(item.getButtonText());
        }

        @Override
        public void unbindView(@NonNull AddTaskItem item) {
            addButton.setText(null);
        }

    }

}

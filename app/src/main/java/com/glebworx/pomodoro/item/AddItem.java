package com.glebworx.pomodoro.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class AddItem extends AbstractItem<AddItem, AddItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

    private String buttonText;


    //                                                                                  CONSTRUCTORS

    public AddItem(String buttonText) {
        this.buttonText = buttonText;
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
        return R.layout.item_add;
    }


    //                                                                                       HELPERS

    public String getButtonText() {
        return buttonText;
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

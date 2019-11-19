package com.glebworx.pomodoro.ui.fragment.view_tasks.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ViewTasksHeaderItem extends AbstractItem<ViewTasksHeaderItem, ViewTasksHeaderItem.ViewHolder> {

    private String projectName;

    public ViewTasksHeaderItem(String projectName) {
        this.projectName = projectName;
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


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewTasksHeaderItem> {

        private AppCompatTextView titleTextView;

        ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.text_view_title);
        }

        @Override
        public void bindView(@NonNull ViewTasksHeaderItem item, @NonNull List<Object> payloads) {
            titleTextView.setText(item.getTitle());
        }

        @Override
        public void unbindView(@NonNull ViewTasksHeaderItem item) {
            titleTextView.setText(null);
        }

    }

}

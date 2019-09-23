package com.glebworx.pomodoro.item;


import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.constants.Constants;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;
import com.triggertrap.seekarc.SeekArc;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

public class ProjectHeaderItem extends AbstractItem<ProjectHeaderItem, ProjectHeaderItem.ViewHolder> implements ISwipeable<ProjectHeaderItem, ProjectHeaderItem> {


    //                                                                                    ATTRIBUTES

    private static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    private int overdueCount;


    //                                                                                  CONSTRUCTORS

    public ProjectHeaderItem() {
        this.overdueCount = 0;
    }

    public ProjectHeaderItem(int overdueCount) {
        this.overdueCount = overdueCount;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_project_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_project_header;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public ProjectHeaderItem withIsSwipeable(boolean swipeable) {
        return this;
    }


    //                                                                                       HELPERS

    public void setOverdueCount(int count) {
        overdueCount = count;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ProjectHeaderItem> {

        private Context context;
        private AppCompatTextView todayTextView;
        private AppCompatTextView thisWeekTextView;
        private AppCompatTextView overdueTextView;

        ViewHolder(View view) {
            super(view);
            context = view.getContext();
            todayTextView = view.findViewById(R.id.text_view_task_count_today);
            thisWeekTextView = view.findViewById(R.id.text_view_task_count_this_week);
            overdueTextView = view.findViewById(R.id.text_view_task_count_overdue);

        }

        @Override
        public void bindView(@NonNull ProjectHeaderItem item, @NonNull List<Object> payloads) {
            int count = item.getOverdueCount();
            overdueTextView.setText(String.valueOf(count));
            if (count > 0) {
                overdueTextView.setTextColor(context.getColor(R.color.colorError));
            } else {
                overdueTextView.setTextColor(context.getColor(R.color.colorHighlight));
            }
        }

        @Override
        public void unbindView(@NonNull ProjectHeaderItem item) {
            todayTextView.setText(null);
            thisWeekTextView.setText(null);
            overdueTextView.setText(null);
            todayTextView.setTextColor(context.getColor(android.R.color.black));
            thisWeekTextView.setTextColor(context.getColor(android.R.color.black));
            overdueTextView.setTextColor(context.getColor(R.color.colorHighlight));
        }

    }

}
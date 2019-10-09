package com.glebworx.pomodoro.ui.fragment.projects;


import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.glebworx.pomodoro.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProjectHeaderItem extends AbstractItem<ProjectHeaderItem, ProjectHeaderItem.ViewHolder> implements ISwipeable<ProjectHeaderItem, ProjectHeaderItem> {


    //                                                                                    ATTRIBUTES

    private static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
    private int todayCount;
    private int thisWeekCount;
    private int overdueCount;
    private View.OnClickListener onClickListener;


    //                                                                                  CONSTRUCTORS

    public ProjectHeaderItem(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        todayCount = 0;
        thisWeekCount = 0;
        overdueCount = 0;
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

    public void setTodayCount(int count) {
        todayCount = count;
    }

    public int getTodayCount() {
        return todayCount;
    }

    public void setThisWeekCount(int count) {
        thisWeekCount = count;
    }

    public int getThisWeekCount() {
        return thisWeekCount;
    }

    public void setOverdueCount(int count) {
        overdueCount = count;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ProjectHeaderItem> {

        private Context context;
        private ConstraintLayout todayLayout;
        private ConstraintLayout thisWeekLayout;
        private ConstraintLayout overdueLayout;
        private AppCompatTextView todayTextView;
        private AppCompatTextView thisWeekTextView;
        private AppCompatTextView overdueTextView;

        ViewHolder(View view) {
            super(view);
            context = view.getContext();
            todayLayout = view.findViewById(R.id.layout_today);
            thisWeekLayout = view.findViewById(R.id.layout_this_week);
            overdueLayout = view.findViewById(R.id.layout_overdue);
            todayTextView = view.findViewById(R.id.text_view_task_count_today);
            thisWeekTextView = view.findViewById(R.id.text_view_task_count_this_week);
            overdueTextView = view.findViewById(R.id.text_view_task_count_overdue);
        }

        @Override
        public void bindView(@NonNull ProjectHeaderItem item, @NonNull List<Object> payloads) {

            int count;

            count = item.getTodayCount();
            todayTextView.setText(String.valueOf(count));
            todayTextView.setTextColor(context.getColor(count > 0 ? android.R.color.black : R.color.colorHighlight));
            todayLayout.setOnClickListener(item.getOnClickListener());

            count = item.getThisWeekCount();
            thisWeekTextView.setText(String.valueOf(count));
            thisWeekTextView.setTextColor(context.getColor(count > 0 ? android.R.color.black : R.color.colorHighlight));
            thisWeekLayout.setOnClickListener(item.getOnClickListener());

            count = item.getOverdueCount();
            overdueTextView.setText(String.valueOf(count));
            overdueTextView.setTextColor(context.getColor(count > 0 ? R.color.colorError : R.color.colorHighlight));
            overdueLayout.setOnClickListener(item.getOnClickListener());

        }

        @Override
        public void unbindView(@NonNull ProjectHeaderItem item) {

            todayTextView.setText(null);
            thisWeekTextView.setText(null);
            overdueTextView.setText(null);

            todayTextView.setTextColor(context.getColor(android.R.color.black));
            thisWeekTextView.setTextColor(context.getColor(android.R.color.black));
            overdueTextView.setTextColor(context.getColor(R.color.colorHighlight));

            todayLayout.setOnClickListener(null);
            thisWeekLayout.setOnClickListener(null);
            overdueLayout.setOnClickListener(null);

        }

    }

}
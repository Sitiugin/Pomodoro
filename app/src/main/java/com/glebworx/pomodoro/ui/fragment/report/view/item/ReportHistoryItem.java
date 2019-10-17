package com.glebworx.pomodoro.ui.fragment.report.view.item;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.HistoryModel;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ReportHistoryItem extends AbstractItem<ReportHistoryItem, ReportHistoryItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

    private HistoryModel model;


    //                                                                                  CONSTRUCTORS

    public ReportHistoryItem(@NonNull HistoryModel model) {
        this.model = model;
    }

    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_history;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_history;
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ReportHistoryItem> {

        private Context context;
        private AppCompatTextView dateTextView;
        private AppCompatTextView eventTypeTextView;

        public ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            this.dateTextView = view.findViewById(R.id.text_view_date);
            this.eventTypeTextView = view.findViewById(R.id.text_view_event_type);
        }

        @Override
        public void bindView(@NonNull ReportHistoryItem item, @NonNull List<Object> payloads) {
            dateTextView.setText(null);
            eventTypeTextView.setText(null);
        }

        @Override
        public void unbindView(@NonNull ReportHistoryItem item) {
            dateTextView.setText(null);
            eventTypeTextView.setText(null);
        }
    }

}

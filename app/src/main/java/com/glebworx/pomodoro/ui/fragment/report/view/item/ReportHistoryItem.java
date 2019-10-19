package com.glebworx.pomodoro.ui.fragment.report.view.item;

import android.content.Context;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.HistoryModel;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_CREATED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_DELETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_UPDATED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_TASK_CREATED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_TASK_DELETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_TASK_UPDATED;

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

    public HistoryModel getModel() {
        return model;
    }

    public String getId() {
        return model.getId();
    }

    private String getEventTypeString(Context context) {
        switch (model.getEventType()) {
            case EVENT_PROJECT_CREATED:
                return context.getString(
                        R.string.report_history_title_project_added,
                        model.getName());
            case EVENT_PROJECT_UPDATED:
                return context.getString(
                        R.string.report_history_title_project_updated,
                        model.getName());
            case EVENT_PROJECT_DELETED:
                return context.getString(
                        R.string.report_history_title_project_deleted,
                        model.getName());
            case EVENT_TASK_CREATED:
                return context.getString(
                        R.string.report_history_title_task_added,
                        model.getTaskName(),
                        model.getName());
            case EVENT_TASK_UPDATED:
                return context.getString(
                        R.string.report_history_title_task_updated,
                        model.getTaskName(),
                        model.getName());
            case EVENT_TASK_DELETED:
                return context.getString(
                        R.string.report_history_title_task_deleted,
                        model.getTaskName(),
                        model.getName());
            default:
                return null;
        }
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ReportHistoryItem> {

        private Context context;
        private AppCompatTextView dateTextView;
        private AppCompatTextView eventTypeTextView;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            this.dateTextView = view.findViewById(R.id.text_view_date);
            this.eventTypeTextView = view.findViewById(R.id.text_view_event_type);
        }

        @Override
        public void bindView(@NonNull ReportHistoryItem item, @NonNull List<Object> payloads) {
            dateTextView.setText(item.getModel().getTimestamp().toString());
            eventTypeTextView.setText(Html.fromHtml(item.getEventTypeString(context), 0));
        }

        @Override
        public void unbindView(@NonNull ReportHistoryItem item) {
            dateTextView.setText(null);
            eventTypeTextView.setText(null);
        }
    }

}

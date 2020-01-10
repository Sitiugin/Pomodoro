package com.glebworx.pomodoro.ui.fragment.report.view.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.glebworx.pomodoro.model.HistoryModel.EVENT_POMODORO_COMPLETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_COMPLETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_CREATED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_DELETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_RESTORED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_UPDATED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_TASK_COMPLETED;
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

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }

    public HistoryModel getModel() {
        return model;
    }

    public Date getTimestamp() {
        return model.getTimestamp();
    }

    private String getColorTag() {
        return model.getColorTag();
    }

    private String getDateString() {
        return DateTimeManager.getHistoryDateString(model.getTimestamp(), new Date());
    }

    private String getEventTypeString(Context context) {
        if (model.getEventType() == null) {
            return "";
        }
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
            case EVENT_PROJECT_COMPLETED:
                return context.getString(
                        R.string.report_history_title_project_completed,
                        model.getName());
            case EVENT_PROJECT_RESTORED:
                return context.getString(
                        R.string.report_history_title_project_restored,
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
            case EVENT_POMODORO_COMPLETED:
                return context.getString(
                        R.string.report_history_title_pomodoro_completed,
                        model.getTaskName(),
                        model.getName());
            case EVENT_TASK_COMPLETED:
                return context.getString(
                        R.string.report_history_title_task_completed,
                        model.getTaskName(),
                        model.getName());
            default:
                return "";
        }
    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ReportHistoryItem> {

        private Context context;
        private Drawable colorTagDrawable;
        private AppCompatTextView dateTextView;
        private AppCompatTextView eventTypeTextView;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            colorTagDrawable = ((LayerDrawable) ((AppCompatImageView) view.findViewById(R.id.view_color_tag))
                    .getDrawable())
                    .findDrawableByLayerId(R.id.shape_color_tag);
            this.dateTextView = view.findViewById(R.id.text_view_date);
            this.eventTypeTextView = view.findViewById(R.id.text_view_event_type);
        }

        @Override
        public void bindView(@NonNull ReportHistoryItem item, @NonNull List<Object> payloads) {
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
            dateTextView.setText(item.getDateString());
            eventTypeTextView.setText(Html.fromHtml(item.getEventTypeString(context), 0));
        }

        @Override
        public void unbindView(@NonNull ReportHistoryItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            dateTextView.setText(null);
            eventTypeTextView.setText(null);
        }
    }

}

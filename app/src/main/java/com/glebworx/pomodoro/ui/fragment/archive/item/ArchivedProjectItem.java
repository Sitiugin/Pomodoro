package com.glebworx.pomodoro.ui.fragment.archive.item;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;

public class ArchivedProjectItem
        extends AbstractItem<ArchivedProjectItem, ArchivedProjectItem.ViewHolder>
        implements
        ISwipeable<ArchivedProjectItem, ArchivedProjectItem> {


    //                                                                                    ATTRIBUTES

    private static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

    private ProjectModel model;
    private static Date currentDate = new Date();


    //                                                                                  CONSTRUCTORS

    public ArchivedProjectItem(@NonNull ProjectModel model) {
        this.model = model;
    }


    //                                                                                    OVERRIDDEN

    @NonNull
    @Override
    public ArchivedProjectItem.ViewHolder getViewHolder(@NonNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.item_archived_project;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_archived_project;
    }

    @Override
    public boolean isSwipeable() {
        return true;
    }

    @Override
    public ArchivedProjectItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArchivedProjectItem)) return false;
        if (!super.equals(o)) return false;
        ArchivedProjectItem that = (ArchivedProjectItem) o;
        return model.equals(that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }


    //                                                                                       HELPERS

    public @Nonnull
    ProjectModel getModel() {
        return this.model;
    }

    public @NonNull
    String getProjectName() {
        return model.getName();
    }

    public @Nullable
    String getColorTag() {
        return model.getColorTag();
    }

    @Nullable
    private String getCompletedOnString(Context context) {
        if (model.getDueDate() == null) {
            return null;
        }
        return DateTimeManager.getCompletedOnString(context, model.getCompletedOn(), currentDate);
    }

    @Nullable
    private String getExtraInfoString(Context context) {
        String estimatedTimeString = DateTimeManager.formatHHMMString(context, model.getEstimatedTime());
        String elapsedTimeString = DateTimeManager.formatHHMMString(context, model.getElapsedTime());
        return context.getString(
                R.string.archive_text_item_extra_info,
                estimatedTimeString,
                elapsedTimeString);

    }


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ArchivedProjectItem> {

        private Context context;
        private Drawable colorTagDrawable;
        private AppCompatTextView titleTextView;
        private AppCompatTextView completedOnTextView;
        private AppCompatTextView extraInfoTextView;

        public ViewHolder(View view) {
            super(view);
            this.context = view.getContext();
            colorTagDrawable = ((LayerDrawable) ((AppCompatImageView) view.findViewById(R.id.view_color_tag))
                    .getDrawable())
                    .findDrawableByLayerId(R.id.shape_color_tag);
            titleTextView = view.findViewById(R.id.text_view_title);
            completedOnTextView = view.findViewById(R.id.text_view_completed_on);
            extraInfoTextView = view.findViewById(R.id.text_view_extra_info);
        }

        @Override
        public void bindView(@NonNull ArchivedProjectItem item, @NonNull List<Object> payloads) {
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));
            titleTextView.setText(item.getProjectName());
            completedOnTextView.setText(item.getCompletedOnString(context));
            extraInfoTextView.setText(item.getExtraInfoString(context));
        }

        @Override
        public void unbindView(@NonNull ArchivedProjectItem item) {
            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            titleTextView.setText(null);
            completedOnTextView.setText(null);
            extraInfoTextView.setText(null);
        }
    }

}

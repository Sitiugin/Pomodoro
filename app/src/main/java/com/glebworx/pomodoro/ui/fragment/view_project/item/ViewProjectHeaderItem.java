package com.glebworx.pomodoro.ui.fragment.view_project.item;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.ISwipeable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ViewProjectHeaderItem extends AbstractItem<ViewProjectHeaderItem, ViewProjectHeaderItem.ViewHolder> implements ISwipeable<ViewProjectHeaderItem, ViewProjectHeaderItem> {


    //                                                                                    ATTRIBUTES

    private static NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale.getDefault());

    private ProjectModel model;


    //                                                                                  CONSTRUCTORS

    public ViewProjectHeaderItem(ProjectModel model) {
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
        return R.id.item_view_project_header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_view_project_header;
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

    @Override
    public ViewProjectHeaderItem withIsSwipeable(boolean swipeable) {
        return this;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViewProjectHeaderItem)) return false;
        ViewProjectHeaderItem that = (ViewProjectHeaderItem) o;
        return model.getEstimatedTime() == that.model.getEstimatedTime()
                && model.getElapsedTime() == that.model.getElapsedTime()
                && model.getProgress() == that.model.getProgress()
                && isSameColorTag(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), model);
    }


    //                                                                                       HELPERS

    public String getColorTag() {
        return this.model.getColorTag();
    }

    public void setColorTag(String colorTag) {
        model.setColorTag(colorTag);
    }

    public int getEstimatedTime() {
        return model.getEstimatedTime();
    }

    public void setEstimatedTime(int estimatedTime) {
        model.setEstimatedTime(estimatedTime);
    }

    public int getElapsedTime() {
        return model.getElapsedTime();
    }

    public void setElapsedTime(int elapsedTime) {
        model.setElapsedTime(elapsedTime);
    }

    public float getProgress() {
        return model.getProgress();
    }

    public void setProgress(float progress) {
        this.model.setProgress(progress);
    }

    private boolean isSameColorTag(ProjectModel model, ProjectModel anotherModel) {
        String colorTag = model.getColorTag();
        String anotherColorTag = anotherModel.getColorTag();
        if (colorTag == null) {
            return anotherColorTag == null;
        }
        return colorTag.equals(anotherColorTag);
    }

    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewProjectHeaderItem> {

        private Context context;
        //private AppCompatImageButton optionsButton;
        private Drawable colorTagDrawable;
        private AppCompatTextView estimatedTimeTextView;
        private AppCompatTextView elapsedTimeTextView;
        private AppCompatTextView progressTextView;

        ViewHolder(View view) {

            super(view);

            context = view.getContext();

            //optionsButton = view.findViewById(R.id.button_options);
            colorTagDrawable = ((LayerDrawable) view.findViewById(R.id.view_color_tag).getBackground())
                    .findDrawableByLayerId(R.id.shape_color_tag);

            estimatedTimeTextView = view.findViewById(R.id.text_view_estimated_time);
            elapsedTimeTextView = view.findViewById(R.id.text_view_elapsed_time);
            progressTextView = view.findViewById(R.id.text_view_progress);

        }

        @Override
        public void bindView(@NonNull ViewProjectHeaderItem item, @NonNull List<Object> payloads) {

            //optionsButton.setOnClickListener(item.getOnClickListener());
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));

            int estimatedTime = item.getEstimatedTime();
            int elapsedTime = item.getElapsedTime();
            float progress = item.getProgress();

            estimatedTimeTextView.setText(DateTimeManager.formatHHMMString(context, estimatedTime));
            elapsedTimeTextView.setText(DateTimeManager.formatHHMMString(context, elapsedTime));
            progressTextView.setText(numberFormat.format(progress > 1 ? 1 : progress));

            elapsedTimeTextView.setTextColor(context.getColor(elapsedTime > estimatedTime ? R.color.colorError : R.color.colorHighlight));

        }

        @Override
        public void unbindView(@NonNull ViewProjectHeaderItem item) {

            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            //optionsButton.setOnClickListener(null);

            estimatedTimeTextView.setText(null);
            elapsedTimeTextView.setText(null);
            progressTextView.setText(null);

            elapsedTimeTextView.setTextColor(context.getColor(R.color.colorHighlight));

        }

    }

}
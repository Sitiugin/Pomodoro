package com.glebworx.pomodoro.ui.main.fragment.view_project;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
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

public class ViewProjectHeaderItem extends AbstractItem<ViewProjectHeaderItem, ViewProjectHeaderItem.ViewHolder> implements ISwipeable<ViewProjectHeaderItem, ViewProjectHeaderItem> {


    //                                                                                    ATTRIBUTES

    private static NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale.getDefault());

    private ProjectModel model;
    private View.OnClickListener onClickListener;
    private int estimatedTime;
    private int elapsedTime;
    private double progress;


    //                                                                                  CONSTRUCTORS

    public ViewProjectHeaderItem(ProjectModel model, View.OnClickListener onClickListener) {
        this.model = model;
        this.onClickListener = onClickListener;
        this.estimatedTime = 0;
        this.elapsedTime = 0;
        this.progress = 0;
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

    public String getColorTag() {
        return this.model.getColorTag();
    }


    //                                                                                       HELPERS

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ViewProjectHeaderItem> {

        private Context context;
        private AppCompatImageButton optionsButton;
        private Drawable colorTagDrawable;
        private AppCompatTextView estimatedTimeTextView;
        private AppCompatTextView elapsedTimeTextView;
        private AppCompatTextView progressTextView;

        ViewHolder(View view) {

            super(view);

            context = view.getContext();

            optionsButton = view.findViewById(R.id.button_options);
            colorTagDrawable = ((LayerDrawable) view.findViewById(R.id.view_color_tag).getBackground())
                    .findDrawableByLayerId(R.id.shape_color_tag);

            estimatedTimeTextView = view.findViewById(R.id.text_view_estimated_time);
            elapsedTimeTextView = view.findViewById(R.id.text_view_elapsed_time);
            progressTextView = view.findViewById(R.id.text_view_progress);

        }

        @Override
        public void bindView(@NonNull ViewProjectHeaderItem item, @NonNull List<Object> payloads) {

            optionsButton.setOnClickListener(item.getOnClickListener());
            colorTagDrawable.setTint(ColorManager.getColor(context, item.getColorTag()));

            estimatedTimeTextView.setText(DateTimeManager.formatHHMMString(context, item.getEstimatedTime()));
            elapsedTimeTextView.setText(DateTimeManager.formatHHMMString(context, item.getElapsedTime()));
            progressTextView.setText(numberFormat.format(item.getProgress()));

        }

        @Override
        public void unbindView(@NonNull ViewProjectHeaderItem item) {

            colorTagDrawable.setTint(ColorManager.getColor(context, null));
            optionsButton.setOnClickListener(null);

            estimatedTimeTextView.setText(null);
            elapsedTimeTextView.setText(null);
            progressTextView.setText(null);

        }

    }

}
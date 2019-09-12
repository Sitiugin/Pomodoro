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
import com.triggertrap.seekarc.SeekArc;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

public class ProjectHeaderItem extends AbstractItem<ProjectHeaderItem, ProjectHeaderItem.ViewHolder> {


    //                                                                                    ATTRIBUTES

    private static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());


    //                                                                                  CONSTRUCTORS

    public ProjectHeaderItem() {
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

    //                                                                                       HELPERS


    //                                                                                   VIEW HOLDER

    protected static class ViewHolder extends FastAdapter.ViewHolder<ProjectHeaderItem> {

        private Context context;

        ViewHolder(View view) {
            super(view);
            this.context = view.getContext();

        }

        @Override
        public void bindView(@NonNull ProjectHeaderItem item, @NonNull List<Object> payloads) {

        }

        @Override
        public void unbindView(@NonNull ProjectHeaderItem item) {

        }

    }

}
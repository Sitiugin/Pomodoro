package com.glebworx.pomodoro.ui.fragment.view_tasks.item;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class ViewTasksHeaderItem extends AbstractItem<ViewTasksHeaderItem, ViewTasksHeaderItem.ViewHolder> {

    private String projectName;
    private ProjectModel projectModel;

    public ViewTasksHeaderItem(String projectName) {
        this.projectName = projectName;
        ProjectApi.getModel(projectName, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                // TODO
            }
        });
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

    public boolean isProjectModelReady() {
        return this.projectModel != null;
    }

    public ProjectModel projectModel() {
        return this.projectModel;
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

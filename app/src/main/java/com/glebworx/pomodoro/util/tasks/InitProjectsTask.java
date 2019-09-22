package com.glebworx.pomodoro.util.tasks;

import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import javax.annotation.Nullable;


public class InitProjectsTask extends AsyncTask<Void, DocumentChange, Void> {

    private QuerySnapshot querySnapshot;
    private ItemAdapter<ProjectItem> itemAdapter;
    private WeakReference<FastAdapter<AbstractItem>> fastAdapterWeakReference;
    private FastAdapter<AbstractItem> fastAdapter;
    private ItemFilter<ProjectItem, ProjectItem> itemFilter;

    public InitProjectsTask(QuerySnapshot querySnapshot,
                            ItemAdapter<ProjectItem> itemAdapter,
                            FastAdapter<AbstractItem> fastAdapter) {
        this.querySnapshot = querySnapshot;
        this.itemAdapter = itemAdapter;
        this.fastAdapterWeakReference = new WeakReference<>(fastAdapter);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (querySnapshot == null) {
            return null;
        }

        List<DocumentChange> changes = querySnapshot.getDocumentChanges();
        for (DocumentChange change: changes) {
            this.publishProgress(change);
        }

        return null;

    }

    @Override
    protected void onProgressUpdate(DocumentChange... values) {

        super.onProgressUpdate(values);

        fastAdapter = fastAdapterWeakReference.get();
        if (itemAdapter == null || fastAdapter == null) {
            cancel(true);
        }

        ProjectItem item;
        int index;

        for (DocumentChange change: values) {
            try {

                item = new ProjectItem(change.getDocument().toObject(ProjectModel.class));
                switch (change.getType()) {
                    case ADDED:
                        //itemAdapter.add(item);
                        itemFilter = itemAdapter.getItemFilter();
                        if (itemFilter  != null) {
                            itemFilter.add(item);
                        } else {
                            itemAdapter.add(item);
                        }
                        //fastAdapter.notifyAdapterItemInserted(itemAdapter.getAdapterItemCount() - 1);
                        break;
                    case MODIFIED:
                        index = getProjectItemIndex(item.getProjectName());
                        if (index != -1) {
                            itemAdapter.set(index + 1, item); // add 1 because of header
                            //fastAdapter.notifyAdapterItemChanged(index);
                        }
                        break;
                    case REMOVED:
                        index = getProjectItemIndex(item.getProjectName());
                        if (index != -1) {
                            //itemAdapter.remove(index + 1); // add 1 because of header
                            itemFilter = itemAdapter.getItemFilter();
                            if (itemFilter  != null) {
                                itemFilter.remove(index + 1);
                            }
                            //fastAdapter.notifyAdapterItemRemoved(index);
                        } else {
                            itemAdapter.remove(index + 1);
                        }
                        break;
                }

            } catch (Exception ignored) { }
        }

    }

    private int getProjectItemIndex(@NonNull String name) {
        return IntStream.range(0, itemAdapter.getAdapterItems().size())
                .filter(i -> name.equals(itemAdapter.getAdapterItems().get(i).getProjectName()))
                .findFirst().orElse(-1);
    }

}

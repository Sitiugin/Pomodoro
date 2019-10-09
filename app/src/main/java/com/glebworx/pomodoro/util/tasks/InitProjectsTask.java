package com.glebworx.pomodoro.util.tasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.IntStream;


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
                        itemAdapter.add(item);
                        break;
                    case MODIFIED:
                        index = getProjectItemIndex(item.getProjectName());
                        if (index != -1) {
                            itemAdapter.set(index + 1, item); // add 1 because of header
                        }
                        break;
                    case REMOVED:
                        index = getProjectItemIndex(item.getProjectName());
                        if (index != -1) {
                            itemAdapter.remove(index + 1); // add 1 because of header
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

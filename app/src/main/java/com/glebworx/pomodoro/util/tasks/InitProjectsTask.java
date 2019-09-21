package com.glebworx.pomodoro.util.tasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;


public class InitProjectsTask extends AsyncTask<Void, DocumentChange, Void> {

    private QuerySnapshot querySnapshot;
    private ItemAdapter<ProjectItem> itemAdapter;

    public InitProjectsTask(QuerySnapshot querySnapshot, ItemAdapter<ProjectItem> itemAdapter) {
        this.querySnapshot = querySnapshot;
        this.itemAdapter = itemAdapter;
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
                            itemAdapter.set(getProjectItemIndex(item.getProjectName()), item);
                        }
                        break;
                    case REMOVED:
                        index = getProjectItemIndex(item.getProjectName());
                        if (index != -1) {
                            itemAdapter.remove(index);
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

package com.glebworx.pomodoro.util.tasks;

import android.os.AsyncTask;

import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.List;

public class InitProjectsTask extends AsyncTask<Void, DocumentChange, Void> {

    private QuerySnapshot querySnapshot;
    private ItemAdapter<ProjectItem> itemAdapter;

    public InitProjectsTask(QuerySnapshot querySnapshot, ItemAdapter<ProjectItem> itemAdapter) {
        this.querySnapshot = querySnapshot;
        this.itemAdapter = itemAdapter;
    }

    /*@Override
    protected void onPreExecute() {
        super.onPreExecute();
        itemAdapter.clear();
    }*/

    @Override
    protected Void doInBackground(Void... voids) {

        if (querySnapshot == null) {
            return null;
        }

        List<DocumentChange> changes = querySnapshot.getDocumentChanges();
        for (DocumentChange change: changes) {
            this.publishProgress(change);
            /*switch (change.getType()) {
                case ADDED:
                    break;
                case MODIFIED:
                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                    break;
                case REMOVED:
                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                    break;
            }*/
        }
        /*List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
        for (DocumentSnapshot snapshot: documentSnapshots) {

            if (!snapshot.exists()) {
                continue;
            }
            ProjectModel model;
            try {
                model = snapshot.toObject(ProjectModel.class);
            } catch (Exception e) {
                continue;
            }
            if (model == null) {
                continue;
            }
            publishProgress(new ProjectItem(model));

        }*/

        return null;

    }

    @Override
    protected void onProgressUpdate(DocumentChange... values) {
        super.onProgressUpdate(values);
        for (DocumentChange change: values) {
            ProjectItem item = new ProjectItem(change.getDocument().toObject(ProjectModel.class));
            switch (change.getType()) {
                case ADDED:
                    itemAdapter.add(item);
                    break;
                case MODIFIED:
                    itemAdapter.set(itemAdapter.getAdapterPosition(item), item);
                    break;
                case REMOVED:
                    itemAdapter.remove(itemAdapter.getAdapterPosition(item));
                    break;
            }
        }
    }

    /*@Override
    protected void onProgressUpdate(ProjectItem... values) {
        super.onProgressUpdate(values);
        itemAdapter.add(values);
    }*/

}

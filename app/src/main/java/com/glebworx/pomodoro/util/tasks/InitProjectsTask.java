package com.glebworx.pomodoro.util.tasks;

import android.os.AsyncTask;

import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.List;

public class InitProjectsTask extends AsyncTask<Void, ProjectItem, Void> {

    private QuerySnapshot querySnapshot;
    private ItemAdapter<ProjectItem> itemAdapter;

    public InitProjectsTask(QuerySnapshot querySnapshot, ItemAdapter<ProjectItem> itemAdapter) {
        this.querySnapshot = querySnapshot;
        this.itemAdapter = itemAdapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        itemAdapter.clear();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (querySnapshot == null) {
            return null;
        }

        List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
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

        }

        return null;

    }

    @Override
    protected void onProgressUpdate(ProjectItem... values) {
        super.onProgressUpdate(values);
        itemAdapter.add(values);
    }

}

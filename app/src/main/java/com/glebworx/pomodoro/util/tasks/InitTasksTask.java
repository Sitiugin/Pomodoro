package com.glebworx.pomodoro.util.tasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.model.TaskModel;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.List;
import java.util.stream.IntStream;


public class InitTasksTask extends AsyncTask<Void, DocumentChange, Void> {

    private QuerySnapshot querySnapshot;
    private ItemAdapter<TaskItem> itemAdapter;

    public InitTasksTask(QuerySnapshot querySnapshot, ItemAdapter<TaskItem> itemAdapter) {
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
        TaskItem item;
        int index;
        for (DocumentChange change: values) {
            item = new TaskItem(change.getDocument().toObject(TaskModel.class));
            switch (change.getType()) {
                case ADDED:
                    itemAdapter.add(item);
                    break;
                case MODIFIED:
                    index = getTaskItemIndex(item.getTaskName());
                    if (index != -1) {
                        itemAdapter.set(getTaskItemIndex(item.getTaskName()), item);
                    }
                    break;
                case REMOVED:
                    index = getTaskItemIndex(item.getTaskName());
                    if (index != -1) {
                        itemAdapter.remove(index);
                    }
                    break;
            }
        }
    }

    private int getTaskItemIndex(@NonNull String name) {
        return IntStream.range(0, itemAdapter.getAdapterItems().size())
                .filter(i -> name.equals(itemAdapter.getAdapterItems().get(i).getTaskName()))
                .findFirst().orElse(-1);
    }

}

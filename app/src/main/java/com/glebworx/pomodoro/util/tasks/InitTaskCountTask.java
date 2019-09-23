package com.glebworx.pomodoro.util.tasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.item.ProjectHeaderItem;
import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;


public class InitTaskCountTask extends AsyncTask<Void, Void, int[]> {

    private QuerySnapshot querySnapshot;
    private ItemAdapter<ProjectHeaderItem> headerAdapter;
    private WeakReference<FastAdapter> fastAdapterWeakReference;

    public InitTaskCountTask(QuerySnapshot querySnapshot,
                             ItemAdapter<ProjectHeaderItem> headerAdapter,
                             FastAdapter fastAdapter) {
        this.querySnapshot = querySnapshot;
        this.headerAdapter = headerAdapter;
        this.fastAdapterWeakReference = new WeakReference<>(fastAdapter);
    }

    @Override
    protected int[] doInBackground(Void... voids) {

        if (querySnapshot == null) {
            return new int[3];
        }

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        DateTimeManager.clearTime(calendar);

        List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
        TaskModel model;
        Date date;
        int[] counts = new int[3];
        for (DocumentSnapshot snapshot: documentSnapshots) {
            model = snapshot.toObject(TaskModel.class);
            if (model == null) {
                continue;
            }
            date = model.getDueDate();
            if (date == null) {
                continue;
            }
            counts[0]++; // TODO
            counts[1]++; // TODO
            if (date.compareTo(calendar.getTime()) < 0) {
                counts[2]++;
            }
        }

        return counts;

    }

    @Override
    protected void onPostExecute(int[] ints) {

        super.onPostExecute(ints);

        ProjectHeaderItem item = headerAdapter.getAdapterItem(0);
        item.setTodayCount(ints[0]);
        item.setThisWeekCount(ints[1]);
        item.setOverdueCount(ints[2]);

        FastAdapter fastAdapter = fastAdapterWeakReference.get();
        if (fastAdapter != null) {
            fastAdapter.notifyAdapterItemChanged(0);
        }

    }
}

package com.glebworx.pomodoro.ui.main.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.util.DummyDataProvider;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ISelectionListener;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.triggertrap.seekarc.SeekArc;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TasksFragment extends Fragment {

    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.seek_arc) SeekArc seekArc;
    @BindView(R.id.text_view_time_remaining) AppCompatTextView timeRemainingTextView;
    @BindView(R.id.text_view_status) AppCompatTextView statusTextView;
    @BindView(R.id.text_view_current_task) AppCompatTextView currentTaskTextView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.fab_start) FloatingActionButton startPomodoroButton;

    private boolean isTaskRunning;

    private ItemAdapter<TaskItem> todayAdapter;

    public TasksFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.bind(this, rootView);

        Context context = getContext();
        Activity activity = getActivity();
        if (context == null || activity == null) {
            return rootView;
        }

        isTaskRunning = false;
        todayAdapter = new ItemAdapter<>();
        todayAdapter.add(DummyDataProvider.getTasks());
        FastAdapter fastAdapter = new FastAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        initRecyclerView(context, activity, layoutManager, fastAdapter);
        initFab();

        return rootView;

    }

    private void initRecyclerView(Context context,
                                  Activity activity,
                                  LinearLayoutManager layoutManager,
                                  FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));

        fastAdapter.addAdapter(0, todayAdapter);
        fastAdapter.setHasStableIds(true);
        fastAdapter.withSelectable(true);
        attachSelectionListener(context, fastAdapter);
        //fastAdapter.withOnClickListener((v, adapter, item, position) -> showEditEntryDialog(context, activity, rootView, (WeightItem) item));
        recyclerView.setAdapter(fastAdapter);
    }

    private void initFab() {
        startPomodoroButton.setOnClickListener(view -> {
            isTaskRunning = !isTaskRunning;
            if (isTaskRunning) {
                startPomodoroButton.setImageResource(R.drawable.ic_pause_black);
                statusTextView.setText(R.string.main_tasks_text_working_on);
            } else {
                startPomodoroButton.setImageResource(R.drawable.ic_play_black);
                statusTextView.setText(R.string.main_tasks_text_paused);
            }
        });
    }

    private void attachSelectionListener(Context context, FastAdapter fastAdapter) {
        fastAdapter.withSelectionListener(new ISelectionListener() {
            @Override
            public void onSelectionChanged(@Nullable IItem item, boolean selected) {

            }
        });
    }

}

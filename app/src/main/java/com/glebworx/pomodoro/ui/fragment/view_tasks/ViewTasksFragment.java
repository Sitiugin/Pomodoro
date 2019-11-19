package com.glebworx.pomodoro.ui.fragment.view_tasks;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragment;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentInteractionListener;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ViewTasksFragment extends Fragment implements IViewTasksFragment {


    //                                                                                       BINDING

    static final String ARG_TYPE = "type";
    public static final String TYPE_TODAY = "today";
    public static final String TYPE_THIS_WEEK = "this_week";
    public static final String TYPE_OVERDUE = "overdue";


    //                                                                                     CONSTANTS

    @BindView(R.id.text_view_subtitle)
    AppCompatTextView subtitleTextView;
    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    //                                                                                    ATTRIBUTES

    private Context context;
    private final Object object = new Object();
    private Map<String, ItemAdapter<TaskItem>> adapterMap;
    private IViewTasksFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ViewTasksFragmentPresenter presenter;
    private FastAdapter<TaskItem> fastAdapter;


    //                                                                                  CONSTRUCTORS

    public ViewTasksFragment() {
    }


    //                                                                                       FACTORY

    public static ViewTasksFragment newInstance(String type) {
        ViewTasksFragment fragment = new ViewTasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_tasks, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ViewTasksFragmentPresenter(
                this,
                fragmentListener,
                getArguments());
        return rootView;
    }

    @Override
    public void onDestroyView() {
        presenter.destroy();
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (IViewTasksFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void onInitView(String type) {
        switch (type) {
            case TYPE_TODAY:
                subtitleTextView.setText(R.string.view_tasks_text_today);
                break;
            case TYPE_THIS_WEEK:
                subtitleTextView.setText(R.string.view_tasks_text_this_week);
                break;
            case TYPE_OVERDUE:
                subtitleTextView.setText(R.string.view_tasks_text_overdue);
                break;
        }
        adapterMap = new HashMap<>();
        fastAdapter = new FastAdapter<>();
        initRecyclerView(fastAdapter);
        initClickEvents(fastAdapter);
    }

    @Override
    public void onTaskAdded(TaskItem item) {
        String taskName = item.getTaskName();
        if (adapterMap.containsKey(taskName)) {
            synchronized (object) {
                ItemAdapter<TaskItem> adapter = adapterMap.get(taskName);
                Objects.requireNonNull(adapter).add(item);
            }
        } else {
            synchronized (object) {
                ItemAdapter<TaskItem> adapter = new ItemAdapter<>();
                adapter.add(item);
                adapterMap.put(taskName, adapter);
                int index = fastAdapter.getItemCount();
                fastAdapter.addAdapter(index, adapter);
                fastAdapter.notifyAdapterItemChanged(index);
            }
        }
    }

    @Override
    public void onTaskModified(TaskItem item) {
        String taskName = item.getTaskName();
        if (adapterMap.containsKey(taskName)) {
            synchronized (object) {
                ItemAdapter<TaskItem> adapter = adapterMap.get(taskName);
                int index = getTaskItemIndex(taskName, Objects.requireNonNull(adapter));
                if (index != -1) {
                    adapter.set(index, item);
                }
            }
        }
    }

    @Override
    public void onTaskDeleted(TaskItem item) {
        String taskName = item.getTaskName();
        if (adapterMap.containsKey(taskName)) {
            synchronized (object) {
                ItemAdapter<TaskItem> adapter = adapterMap.get(taskName);
                int index = getTaskItemIndex(taskName, Objects.requireNonNull(adapter));
                if (index != -1) {
                    adapter.remove(index);
                }
            }
        }
    }

    @Override
    public void onTaskCompleted(TaskItem item, CompletedTaskItem completedItem) {

    }


    //                                                                                       HELPERS

    private void initRecyclerView(FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new AlphaCrossFadeAnimator());

        fastAdapter.setHasStableIds(true);
        fastAdapter.withSelectable(true);
        recyclerView.setAdapter(fastAdapter);

    }

    private void initClickEvents(FastAdapter<TaskItem> fastAdapter) {
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_close) {
                fragmentListener.onCloseFragment();
            }
        };
        closeButton.setOnClickListener(onClickListener);
        fastAdapter.withOnClickListener((view, adapter, item, position) -> {
            if (view == null || !item.isEnabled()) {
                return false;
            }
            if (view.getId() == R.id.item_task) {
                presenter.selectTask(item);
                return true;
            }
            return false;
        });
    }

    private int getTaskItemIndex(@NonNull String name, @NonNull ItemAdapter<TaskItem> adapter) {
        return IntStream.range(0, adapter.getAdapterItems().size())
                .filter(i -> name.equals(adapter.getAdapterItems().get(i).getTaskName()))
                .findFirst().orElse(-1);
    }

}

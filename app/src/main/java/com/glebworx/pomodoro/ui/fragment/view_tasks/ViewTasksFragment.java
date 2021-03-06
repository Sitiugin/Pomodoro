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
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragment;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_tasks.item.ViewTasksFooterItem;
import com.glebworx.pomodoro.ui.fragment.view_tasks.item.ViewTasksHeaderItem;
import com.glebworx.pomodoro.ui.item.TaskItem;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

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
    private Map<String, ItemAdapter<AbstractItem>> headerAdapterMap;
    private Map<String, ItemAdapter<AbstractItem>> footerAdapterMap;
    private Map<String, ItemAdapter<AbstractItem>> adapterMap;
    private Map<String, ItemAdapter<AbstractItem>> completedAdapterMap;
    private IViewTasksFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ViewTasksFragmentPresenter presenter;
    private FastAdapter<AbstractItem> fastAdapter;
    private int adapterCount;


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
                subtitleTextView.setTextColor(context.getColor(R.color.colorError));
                break;
        }

        headerAdapterMap = new HashMap<>();
        footerAdapterMap = new HashMap<>();
        adapterMap = new HashMap<>();
        completedAdapterMap = new HashMap<>();
        fastAdapter = new FastAdapter<>();
        adapterCount = 0;

        initRecyclerView(fastAdapter);
        initClickEvents(fastAdapter);

    }

    @Override
    public synchronized void onTaskAdded(TaskItem item) {

        String projectName = item.getProjectName();

        if (adapterMap.containsKey(projectName)) { // project already present

            ItemAdapter<AbstractItem> adapter = adapterMap.get(projectName);
            Objects.requireNonNull(adapter).add(item);

        } else {

            ItemAdapter<AbstractItem> headerAdapter = new ItemAdapter<>();
            ItemAdapter<AbstractItem> adapter = new ItemAdapter<>();
            ItemAdapter<AbstractItem> completedAdapter = new ItemAdapter<>();
            ItemAdapter<AbstractItem> footerAdapter = new ItemAdapter<>();

            headerAdapter.add(new ViewTasksHeaderItem(item.getProjectName()));
            headerAdapterMap.put(projectName, headerAdapter);
            footerAdapterMap.put(projectName, footerAdapter);

            adapter.add(item);
            adapterMap.put(projectName, adapter);

            completedAdapterMap.put(projectName, completedAdapter);

            footerAdapter.add(new ViewTasksFooterItem());

            fastAdapter.addAdapter(adapterCount++, headerAdapter);
            fastAdapter.addAdapter(adapterCount++, adapter);
            fastAdapter.addAdapter(adapterCount++, completedAdapter);
            fastAdapter.addAdapter(adapterCount++, footerAdapter);
            fastAdapter.notifyAdapterItemRangeChanged(adapterCount, 4);

        }

    }

    @Override
    public synchronized void onTaskModified(TaskItem item) {
        String projectName = item.getProjectName();
        if (adapterMap.containsKey(projectName)) {
            ItemAdapter<AbstractItem> adapter = adapterMap.get(projectName);
            int index = getTaskItemIndex(item.getTaskName(), Objects.requireNonNull(adapter));
            if (index != -1) {
                adapter.set(index + 1, item);
            }
        }
    }

    @Override
    public synchronized void onTaskDeleted(TaskItem item) {
        String projectName = item.getProjectName();
        if (adapterMap.containsKey(projectName)) {
            ItemAdapter<AbstractItem> adapter = adapterMap.get(projectName);
            int index = getTaskItemIndex(item.getTaskName(), Objects.requireNonNull(adapter));
            if (index != -1) {
                adapter.remove(index + 1);
            }
        }
    }

    @Override
    public synchronized void onTaskCompleted(CompletedTaskItem completedItem) {

        String projectName = completedItem.getProjectName();

        if (adapterMap.containsKey(projectName)) {
            ItemAdapter<AbstractItem> adapter = adapterMap.get(projectName);
            int index = getTaskItemIndex(completedItem.getTaskName(), Objects.requireNonNull(adapter));
            if (index != -1) {
                adapter.remove(index + 1);
            }
        }

        if (completedAdapterMap.containsKey(projectName)) {

            ItemAdapter<AbstractItem> adapter = completedAdapterMap.get(projectName);
            Objects.requireNonNull(adapter).add(completedItem);

        } else {

            ItemAdapter<AbstractItem> headerAdapter = new ItemAdapter<>();
            ItemAdapter<AbstractItem> adapter = new ItemAdapter<>();
            ItemAdapter<AbstractItem> completedAdapter = new ItemAdapter<>();
            ItemAdapter<AbstractItem> footerAdapter = new ItemAdapter<>();

            headerAdapter.add(new ViewTasksHeaderItem(completedItem.getProjectName()));
            headerAdapterMap.put(projectName, headerAdapter);
            footerAdapterMap.put(projectName, footerAdapter);

            adapterMap.put(projectName, adapter);

            completedAdapter.add(completedItem);
            completedAdapterMap.put(projectName, completedAdapter);

            footerAdapter.add(new ViewTasksFooterItem());

            fastAdapter.addAdapter(adapterCount++, headerAdapter);
            fastAdapter.addAdapter(adapterCount++, adapter);
            fastAdapter.addAdapter(adapterCount++, completedAdapter);
            fastAdapter.addAdapter(adapterCount++, footerAdapter);
            fastAdapter.notifyAdapterItemRangeChanged(adapterCount, 4);

        }

    }

    @Override
    public synchronized void onProjectChanged(ProjectModel projectModel) { // TODO view should not know the model

        String projectName = projectModel.getName();

        ItemAdapter<AbstractItem> headerAdapter = headerAdapterMap.get(projectName);
        if (headerAdapter != null) {
            AbstractItem headerItem = Objects.requireNonNull(headerAdapter).getAdapterItem(0);
            ((ViewTasksHeaderItem) headerItem).setColorTag(projectModel.getColorTag());
        }

        ItemAdapter<AbstractItem> adapter = adapterMap.get(projectName);
        if (adapter != null) {
            TaskItem taskItem;
            for (AbstractItem item : adapter.getAdapterItems()) {
                taskItem = ((TaskItem) item);
                taskItem.setProjectModel(projectModel);
            }
        }

        ItemAdapter<AbstractItem> footerAdapter = footerAdapterMap.get(projectName);
        if (footerAdapter != null) {
            ViewTasksFooterItem footerItem = (ViewTasksFooterItem) Objects.requireNonNull(footerAdapter).getAdapterItem(0);
            footerItem.setModel(projectModel);
        }

        fastAdapter.notifyAdapterDataSetChanged();

    }

    @Override
    public synchronized void onProjectDeleted(String projectName) {

        ItemAdapter<AbstractItem> headerAdapter = headerAdapterMap.get(projectName);
        if (headerAdapter != null) {
            headerAdapter.clear();
        }

        ItemAdapter<AbstractItem> adapter = adapterMap.get(projectName);
        if (adapter != null) {
            adapter.clear();
        }

        ItemAdapter<AbstractItem> completedAdapter = completedAdapterMap.get(projectName);
        if (completedAdapter != null) {
            completedAdapter.clear();
        }

        ItemAdapter<AbstractItem> footerAdapter = footerAdapterMap.get(projectName);
        if (footerAdapter != null) {
            footerAdapter.clear();
        }

        fastAdapter.notifyAdapterDataSetChanged();

    }


    //                                                                                       HELPERS

    private void initRecyclerView(FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty_tasks));
        recyclerView.setItemAnimator(new SlideInOutLeftAnimator(recyclerView));

        fastAdapter.setHasStableIds(true);
        recyclerView.setAdapter(fastAdapter);

    }

    private void initClickEvents(FastAdapter<AbstractItem> fastAdapter) {
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
                presenter.selectTask((TaskItem) item);
                return true;
            } else if (view.getId() == R.id.item_view_tasks_header) {
                presenter.viewProject(((ViewTasksHeaderItem) item).getTitle());
                return true;
            }
            return false;
        });
    }

    private int getTaskItemIndex(@NonNull String name, @NonNull ItemAdapter<AbstractItem> adapter) {
        return IntStream.range(0, adapter.getAdapterItems().size())
                .filter(i -> name.equals(((TaskItem) adapter.getAdapterItems().get(i)).getTaskName()))
                .findFirst().orElse(-1);
    }

}

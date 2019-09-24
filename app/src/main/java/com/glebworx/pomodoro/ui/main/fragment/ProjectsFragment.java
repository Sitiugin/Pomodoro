package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.item.AddItem;
import com.glebworx.pomodoro.item.ProjectHeaderItem;
import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;
import com.glebworx.pomodoro.util.tasks.InitProjectsTask;
import com.glebworx.pomodoro.util.tasks.InitTaskCountTask;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO add project when filter is on is wrong
// TODO overdue projects and tasks must be red
public class ProjectsFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.button_report) AppCompatImageButton reportButton;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    //                                                                                    ATTRIBUTES

    private ItemAdapter<ProjectHeaderItem> headerAdapter;
    private ItemAdapter<ProjectItem> projectAdapter;
    private FastAdapter<AbstractItem> fastAdapter;
    private EventListener<QuerySnapshot> taskCountEventListener;
    private EventListener<QuerySnapshot> projectsEventListener;
    private OnProjectFragmentInteractionListener fragmentListener;
    private InitTaskCountTask initTaskCountTask;
    private InitProjectsTask initProjectsTask;


    //                                                                                     LIFECYCLE

    public ProjectsFragment() { }

    public static ProjectsFragment newInstance() {
        return new ProjectsFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_projects, container, false);
        ButterKnife.bind(this, rootView);

        Context context = getContext();
        if (context == null) {
            return rootView;
        }

        //projectAdapter.add(DummyDataProvider.getProjects());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        initRecyclerView(context, layoutManager, fastAdapter);
        initSearchView();
        initClickEvents(context, fastAdapter);

        TaskApi.addAllTasksEventListener(taskCountEventListener);
        ProjectApi.addModelEventListener(projectsEventListener);

        return rootView;

    }

    @Override
    public void onAttach(@NonNull Context context) {

        projectAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();

        super.onAttach(context);

        taskCountEventListener = (snapshots, e) -> { // TODO not triggered when editing a project task
            if (initTaskCountTask != null && initTaskCountTask.getStatus() != AsyncTask.Status.FINISHED) {
                initTaskCountTask.cancel(true);
            }
            if (snapshots == null) {
                return;
            }
            initTaskCountTask = new InitTaskCountTask(snapshots, headerAdapter, fastAdapter);
            initTaskCountTask.execute();
        };
        projectsEventListener = (snapshots, e) -> {
            if (initProjectsTask != null && initProjectsTask.getStatus() != AsyncTask.Status.FINISHED) {
                initProjectsTask.cancel(true);
            }
            if (snapshots == null) {
                return;
            }
            initProjectsTask = new InitProjectsTask(snapshots, projectAdapter, fastAdapter);
            initProjectsTask.execute();
        };

        fragmentListener = (OnProjectFragmentInteractionListener) context;

    }

    @Override
    public void onDetach() {
        taskCountEventListener = null;
        projectsEventListener = null;
        fragmentListener = null;
        super.onDetach();
    }

    private void initRecyclerView(Context context, LinearLayoutManager layoutManager, FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new SlideInOutLeftAnimator(recyclerView));
        //OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        headerAdapter = new ItemAdapter<>();
        headerAdapter.add(new ProjectHeaderItem());
        ItemAdapter<AddItem> addAdapter = new ItemAdapter<>();
        addAdapter.add(new AddItem(getString(R.string.add_project_title_add_project)));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, projectAdapter);
        fastAdapter.addAdapter(2, addAdapter);

        fastAdapter.setHasStableIds(true);
        attachSwipeHelper(context, recyclerView);
        recyclerView.setAdapter(fastAdapter);

    }

    private void attachSwipeHelper(Context context,
                                   RecyclerView recyclerView) {
        SimpleSwipeCallback swipeCallback = new SimpleSwipeCallback(
                (position, direction) -> {
                    ProjectModel projectModel = projectAdapter.getAdapterItem(position - 1).getModel();
                    if (direction == ItemTouchHelper.RIGHT) {
                        fragmentListener.onEditProject(projectModel);
                        fastAdapter.notifyAdapterItemChanged(position);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        deleteProject(context, projectModel, position);
                    }
                },
                context.getDrawable(R.drawable.ic_delete_black),
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                context.getColor(android.R.color.transparent))
                .withLeaveBehindSwipeRight(context.getDrawable(R.drawable.ic_edit_black));
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteProject(Context context, ProjectModel projectModel, int position) {
        searchView.setQuery(null, true);
        ProjectApi.deleteProject(projectModel, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, R.string.view_project_toast_project_delete_success, Toast.LENGTH_SHORT).show();
            } else {
                fastAdapter.notifyAdapterItemChanged(position);
                Toast.makeText(context, R.string.view_project_toast_project_delete_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initSearchView() {

        IItemAdapter.Predicate<ProjectItem> predicate = (item, constraint) -> {
            if (constraint == null) {
                return true;
            }
            String title = item.getModel().getName();
            return title != null && title.toLowerCase().contains(constraint);
        };

        ItemFilter<ProjectItem, ProjectItem> itemFilter =
                new ItemFilter<ProjectItem, ProjectItem>(projectAdapter).withFilterPredicate(predicate);

        projectAdapter.withItemFilter(itemFilter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                projectAdapter.filter(newText);
                return true;
            }
        });

    }

    private void initClickEvents(Context context, FastAdapter<AbstractItem> fastAdapter) {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_report:
                    fragmentListener.onViewReport();
                    break;
                case R.id.button_options:
                    showOptionsPopup(context);
                    break;
            }
        };
        reportButton.setOnClickListener(onClickListener);
        optionsButton.setOnClickListener(onClickListener);
        fastAdapter.withOnClickListener((view, adapter, item, position) -> {
            if (view == null) {
                return false;
            }
            if (view.getId() == R.id.item_project && item instanceof ProjectItem) {
                fragmentListener.onViewProject(((ProjectItem) item).getModel());
                return true;
            }
            if (view.getId() == R.id.item_add) {
                searchView.setQuery(null, true);
                fragmentListener.onAddProject();
                return true;
            }
            return false;
        });
    }

    private void setOverdueCount(int count) {
        headerAdapter.getAdapterItem(0).setOverdueCount(count);
        fastAdapter.notifyAdapterItemChanged(0);
    }

    private void showOptionsPopup(Context context) {
        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.showPopup(
                R.layout.popup_options_projects,
                optionsButton,
                Gravity.BOTTOM | Gravity.END);
        /*popupWindow.getContentView().setOnClickListener(view -> {
            switch (view.getId()) {
                case R.id.button_settings:
                    popupWindow.dismiss();
                    break;
            }
        });*/
    }

    public interface OnProjectFragmentInteractionListener {
        void onAddProject();
        void onViewProject(ProjectModel projectModel);
        void onViewReport();
        void onEditProject(ProjectModel projectModel);
    }

}

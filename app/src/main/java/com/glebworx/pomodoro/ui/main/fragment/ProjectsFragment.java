package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.item.AddItem;
import com.glebworx.pomodoro.item.ProjectHeaderItem;
import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.tasks.InitProjectsTask;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.itemanimators.AlphaInAnimator;
import com.mikepenz.itemanimators.SlideInOutBottomAnimator;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;
import com.mikepenz.itemanimators.SlideInOutTopAnimator;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class ProjectsFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.button_report) AppCompatImageButton reportButton;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    //                                                                                    ATTRIBUTES

    private ItemAdapter<ProjectItem> projectAdapter;
    private EventListener<QuerySnapshot> eventListener;
    private OnProjectFragmentInteractionListener fragmentListener;
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
        FastAdapter<AbstractItem> fastAdapter = new FastAdapter<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        initRecyclerView(layoutManager, fastAdapter);
        initSearchView();
        initClickEvents(fastAdapter);

        ProjectApi.addModelEventListener(eventListener);

        return rootView;

    }

    @Override
    public void onAttach(@NonNull Context context) { // TODO is this null safe
        projectAdapter = new ItemAdapter<>();
        if (initProjectsTask != null && initProjectsTask.getStatus() != AsyncTask.Status.FINISHED) {
            initProjectsTask.cancel(true);
        }
        super.onAttach(context);
        eventListener = (snapshots, e) -> {
            initProjectsTask = new InitProjectsTask(snapshots, projectAdapter);
            initProjectsTask.execute();
        };
        fragmentListener = (OnProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        eventListener = null;
        fragmentListener = null;
        super.onDetach();
    }

    private void initRecyclerView(LinearLayoutManager layoutManager, FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new SlideInOutLeftAnimator(recyclerView));
        //OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        ItemAdapter<ProjectHeaderItem> headerAdapter = new ItemAdapter<>();
        headerAdapter.add(new ProjectHeaderItem());
        ItemAdapter<AddItem> addAdapter = new ItemAdapter<>();
        addAdapter.add(new AddItem(getString(R.string.add_project_title_add_project)));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, projectAdapter);
        fastAdapter.addAdapter(2, addAdapter);

        fastAdapter.setHasStableIds(true);
        recyclerView.setAdapter(fastAdapter);

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

    private void initClickEvents(FastAdapter<AbstractItem> fastAdapter) {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_report:
                    break;
                case R.id.button_options:
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
                fragmentListener.onViewProjectClicked(((ProjectItem) item).getModel());
                return true;
            }
            if (view.getId() == R.id.item_add) {
                fragmentListener.onAddProjectClicked();
                return true;
            }
            return false;
        });
    }

    public interface OnProjectFragmentInteractionListener {
        void onAddProjectClicked();
        void onViewProjectClicked(ProjectModel projectModel);
    }

}

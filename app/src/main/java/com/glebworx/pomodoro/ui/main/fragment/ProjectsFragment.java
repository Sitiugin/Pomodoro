package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
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
import android.widget.Toast;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.item.AddItem;
import com.glebworx.pomodoro.item.ProjectHeaderItem;
import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.ui.main.activity.MainActivity;
import com.glebworx.pomodoro.util.DummyDataProvider;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProjectsFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.button_report) AppCompatImageButton reportButton;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    //                                                                                    ATTRIBUTES

    private ItemAdapter<ProjectItem> projectAdapter;
    private OnProjectFragmentInteractionListener listener;


    //                                                                                     LIFECYCLE

    public ProjectsFragment() { }


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

        projectAdapter = new ItemAdapter<>();
        projectAdapter.add(DummyDataProvider.getProjects());
        FastAdapter<AbstractItem> fastAdapter = new FastAdapter<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        initRecyclerView(layoutManager, fastAdapter);
        initSearchView();
        initClickEvents(fastAdapter);

        return rootView;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    private void initRecyclerView(LinearLayoutManager layoutManager, FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));

        ItemAdapter<ProjectHeaderItem> headerAdapter = new ItemAdapter<>();
        headerAdapter.add(new ProjectHeaderItem());
        ItemAdapter<AddItem> footerAdapter = new ItemAdapter<>();
        footerAdapter.add(new AddItem(getString(R.string.main_title_add_project)));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, projectAdapter);
        fastAdapter.addAdapter(2, footerAdapter);

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
            if (view.getId() == R.id.item_add) {
                listener.onAddProjectClicked();
                return true;
            }
            return false;
        });
    }

    public interface OnProjectFragmentInteractionListener {
        void onAddProjectClicked();
    }

}

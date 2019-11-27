package com.glebworx.pomodoro.ui.fragment.archive;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.item.ProjectItem;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.widget.Toast.LENGTH_LONG;
import static com.glebworx.pomodoro.util.constants.Constants.LENGTH_SNACK_BAR;


public class ArchiveFragment extends Fragment implements IArchiveFragment {


    //                                                                                       BINDING

    @BindView(R.id.button_delete_all)
    AppCompatImageButton deleteAllButton;
    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    //                                                                                    ATTRIBUTES

    private Context context;
    private ItemAdapter<ProjectItem> projectAdapter;
    private FastAdapter<AbstractItem> fastAdapter;
    private UndoHelper<AbstractItem> undoHelper;
    private IArchiveFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ArchiveFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public ArchiveFragment() {
    }

    public static ArchiveFragment newInstance() {
        return new ArchiveFragment();
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_archive, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ArchiveFragmentPresenter(this, fragmentListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (IArchiveFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                     INTERFACE

    @Override
    public void onInitView(IItemAdapter.Predicate<ProjectItem> predicate) {

        projectAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        undoHelper = new UndoHelper<>(fastAdapter, (positions, removed) -> {
            for (FastAdapter.RelativeInfo<AbstractItem> relativeInfo : removed) {
                presenter.deleteProject((ProjectItem) relativeInfo.item, relativeInfo.position);
            }
        });

        initRecyclerView(fastAdapter);
        initSearchView(predicate);
        initClickEvents(fastAdapter);

    }

    @Override
    public void onItemAdded(ProjectItem item) {

    }

    @Override
    public void onItemModified(ProjectItem item) {

    }

    @Override
    public void onItemDeleted(ProjectItem item) {

    }

    @Override
    public void onDeleteProjectFailed(int position) {
        synchronized (this) {
            fastAdapter.notifyAdapterItemChanged(position);
        }
        Toast.makeText(context, R.string.archive_toast_project_delete_failed, LENGTH_LONG).show();
    }


    //                                                                                       HELPERS

    private void initRecyclerView(FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new SlideInOutLeftAnimator(recyclerView));
        //OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        fastAdapter.addAdapter(0, projectAdapter);

        fastAdapter.setHasStableIds(true);
        attachSwipeHelper(recyclerView);
        recyclerView.setAdapter(fastAdapter);

    }

    private void attachSwipeHelper(RecyclerView recyclerView) {
        SimpleSwipeCallback swipeCallback = new SimpleSwipeCallback(
                (position, direction) -> {

                    searchView.clearFocus();

                    ProjectItem item = projectAdapter.getAdapterItem(position - 1);

                    if (direction == ItemTouchHelper.RIGHT) {
                        presenter.restoreProject(item);
                        // TODO implement
                        //fastAdapter.notifyAdapterItemChanged(position);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        Set<Integer> positionSet = new HashSet<>();
                        positionSet.add(position);
                        searchView.setEnabled(false);
                        undoHelper.remove(
                                recyclerView,
                                getString(R.string.projects_toast_project_delete_success),
                                getString(R.string.core_undo),
                                LENGTH_SNACK_BAR,
                                positionSet);
                    }

                },
                context.getDrawable(R.drawable.ic_delete_red),
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                context.getColor(android.R.color.transparent))
                .withLeaveBehindSwipeRight(context.getDrawable(R.drawable.ic_restore_black));
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initSearchView(IItemAdapter.Predicate<ProjectItem> predicate) {

        ItemFilter<ProjectItem, ProjectItem> itemFilter =
                new ItemFilter<ProjectItem, ProjectItem>(projectAdapter).withFilterPredicate(predicate);
        projectAdapter.withItemFilter(itemFilter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (undoHelper.getSnackBar() != null && undoHelper.getSnackBar().isShown()) {
                    undoHelper.getSnackBar().dismiss();
                }
                projectAdapter.filter(newText);
                return true;
            }
        });

    }

    private void initClickEvents(FastAdapter<AbstractItem> fastAdapter) {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_delete_all:
                    // TODO implement
                    break;
                case R.id.button_close:
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        deleteAllButton.setOnClickListener(onClickListener);
        closeButton.setOnClickListener(onClickListener);
        /*fastAdapter.withOnClickListener((view, adapter, item, position) -> {
            if (view == null) {
                return false;
            }
            if (view.getId() == R.id.item_project && item instanceof ProjectItem) {
                presenter.viewProject((ProjectItem) item);
                return true;
            }
            return false;
        });*/
    }

}

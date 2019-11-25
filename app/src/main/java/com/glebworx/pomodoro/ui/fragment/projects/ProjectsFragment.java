package com.glebworx.pomodoro.ui.fragment.projects;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragment;
import com.glebworx.pomodoro.ui.fragment.projects.interfaces.IProjectsFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.projects.item.AddProjectItem;
import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectHeaderItem;
import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.itemanimators.DefaultAnimator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.widget.Toast.LENGTH_LONG;
import static com.glebworx.pomodoro.util.constants.Constants.LENGTH_SNACK_BAR;


public class ProjectsFragment extends Fragment implements IProjectsFragment {


    //                                                                                       BINDING

    @BindView(R.id.button_report) AppCompatImageButton reportButton;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.search_view) SearchView searchView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    //                                                                                    ATTRIBUTES

    private Context context;
    private ItemAdapter<ProjectHeaderItem> headerAdapter;
    private ItemAdapter<ProjectItem> projectAdapter;
    private FastAdapter<AbstractItem> fastAdapter;
    private UndoHelper<AbstractItem> undoHelper;
    private IProjectsFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ProjectsFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public ProjectsFragment() { }

    public static ProjectsFragment newInstance() {
        return new ProjectsFragment();
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_projects, container, false);
        context = getContext();
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ProjectsFragmentPresenter(this, fragmentListener);
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
        fragmentListener = (IProjectsFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }

    /*@Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            presenter.refreshTasksHeader();
        }
    }*/

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
        synchronized (this) {
            projectAdapter.add(item);
        }
    }

    @Override
    public void onItemModified(ProjectItem item) {
        synchronized (this) {
            int index = getProjectItemIndex(item.getProjectName());
            if (index != -1) {
                projectAdapter.set(index + 1, item); // add 1 because of header
            }
        }
    }

    @Override
    public void onItemDeleted(ProjectItem item) {
        synchronized (this) {
            int index = getProjectItemIndex(item.getProjectName());
            if (index != -1) {
                projectAdapter.remove(index + 1); // add 1 because of header
            }
        }
    }

    @Override
    public void onTodayTaskCountChanged(int todayTaskCount) {
        synchronized (this) {
            ProjectHeaderItem item = headerAdapter.getAdapterItem(0);
            if (item != null) {
                item.setTodayCount(todayTaskCount);
                fastAdapter.notifyAdapterItemChanged(0);
            }
        }
    }

    @Override
    public void onThisWeekTaskCountChanged(int thisWeekTaskCount) {
        synchronized (this) {
            ProjectHeaderItem item = headerAdapter.getAdapterItem(0);
            if (item != null) {
                item.setThisWeekCount(thisWeekTaskCount);
                fastAdapter.notifyAdapterItemChanged(0);
            }
        }
    }

    @Override
    public void onOverdueTaskCountChanged(int overdueTaskCount) {
        synchronized (this) {
            ProjectHeaderItem item = headerAdapter.getAdapterItem(0);
            if (item != null) {
                item.setOverdueCount(overdueTaskCount);
                fastAdapter.notifyAdapterItemChanged(0);
            }
        }
    }

    @Override
    public void onDeleteProjectFailed(int position) {
        synchronized (this) {
            fastAdapter.notifyAdapterItemChanged(position);
        }
        Toast.makeText(context, R.string.view_project_toast_project_delete_failed, LENGTH_LONG).show();
    }


    //                                                                                       HELPERS

    private void initRecyclerView(FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultAnimator<>());
        //OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        headerAdapter = new ItemAdapter<>();
        headerAdapter.add(new ProjectHeaderItem(view -> {
            switch (view.getId()) {
                case R.id.layout_today:
                    fragmentListener.onViewTodayTasks();
                    break;
                case R.id.layout_this_week:
                    fragmentListener.onViewThisWeekTasks();
                    break;
                case R.id.layout_overdue:
                    fragmentListener.onViewOverdueTasks();
                    break;
            }
        }));
        ItemAdapter<AddProjectItem> addAdapter = new ItemAdapter<>();
        addAdapter.add(new AddProjectItem(getString(R.string.add_project_title_add_project)));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, projectAdapter);
        fastAdapter.addAdapter(2, addAdapter);

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
                        presenter.editProject(item);
                        fastAdapter.notifyAdapterItemChanged(position);
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
                .withLeaveBehindSwipeRight(context.getDrawable(R.drawable.ic_edit_black));
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private int getProjectItemIndex(@NonNull String name) {
        return IntStream.range(0, projectAdapter.getAdapterItems().size())
                .filter(i -> name.equals(projectAdapter.getAdapterItems().get(i).getProjectName()))
                .findFirst().orElse(-1);
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
                case R.id.button_report:
                    fragmentListener.onViewReport();
                    break;
                case R.id.button_options:
                    showOptionsPopup();
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
                presenter.viewProject((ProjectItem) item);
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

    private void showOptionsPopup() {
        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.showPopup(
                R.layout.popup_options_projects,
                optionsButton,
                Gravity.BOTTOM | Gravity.END);
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_about:
                    popupWindow.dismiss();
                    fragmentListener.onViewAboutInfo();
                    break;
                case R.id.button_send_feedback:
                    popupWindow.dismiss();
                    presenter.sendFeedback(context);
                    break;
                case R.id.button_sign_out:
                    popupWindow.dismiss();
                    showSignOutDialog();
                    break;
            }
        };
        View contentView = popupWindow.getContentView();
        contentView.findViewById(R.id.button_about).setOnClickListener(onClickListener);
        contentView.findViewById(R.id.button_send_feedback).setOnClickListener(onClickListener);
        contentView.findViewById(R.id.button_sign_out).setOnClickListener(onClickListener);
    }

    private void showSignOutDialog() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        DialogManager.showGenericDialog(
                activity,
                R.id.container_main,
                R.string.projects_title_sign_out,
                R.string.projects_text_sign_out,
                R.string.projects_title_sign_out,
                () -> presenter.signOut(context));
    }

}

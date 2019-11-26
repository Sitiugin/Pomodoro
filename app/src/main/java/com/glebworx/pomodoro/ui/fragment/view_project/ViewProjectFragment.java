package com.glebworx.pomodoro.ui.fragment.view_project;


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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragment;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_project.item.AddTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;
import com.glebworx.pomodoro.ui.item.TaskItem;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.itemanimators.DefaultAnimator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.glebworx.pomodoro.util.constants.Constants.LENGTH_SNACK_BAR;


// TODO add board and calendar view
public class ViewProjectFragment extends Fragment implements IViewProjectFragment {


    //                                                                                       BINDING

    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.text_view_subtitle) AppCompatTextView subtitleTextView;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.fab_complete)
    FloatingActionButton completeButton;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    //                                                                                     CONSTANTS

    static final String ARG_PROJECT_MODEL = "project_model";


    //                                                                                    ATTRIBUTES

    private Context context;
    private FastAdapter<AbstractItem> fastAdapter;
    private ItemAdapter<ViewProjectHeaderItem> headerAdapter;
    private ItemAdapter<TaskItem> taskAdapter;
    private ItemAdapter<CompletedTaskItem> completedTaskAdapter;
    private UndoHelper<AbstractItem> undoHelper;
    private IViewProjectFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ViewProjectFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public ViewProjectFragment() { }


    //                                                                                       FACTORY

    public static ViewProjectFragment newInstance(ProjectModel projectModel) {
        ViewProjectFragment fragment = new ViewProjectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        fragment.setArguments(args);
        return fragment;
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_project, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ViewProjectFragmentPresenter(
                this,
                fragmentListener,
                getArguments(),
                getHeaderClickListener());
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
        fragmentListener = (IViewProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void onInitView(String projectName,
                           boolean allTasksCompleted,
                           boolean isCompleted,
                           ViewProjectHeaderItem headerItem) {
        headerAdapter = new ItemAdapter<>();
        taskAdapter = new ItemAdapter<>();
        completedTaskAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        undoHelper = new UndoHelper<>(fastAdapter, (positions, removed) -> {
            for (FastAdapter.RelativeInfo<AbstractItem> relativeInfo : removed) {
                presenter.deleteTask(((TaskItem) relativeInfo.item), relativeInfo.position);
            }
        });
        titleTextView.setText(projectName);
        completeButton.setVisibility(allTasksCompleted && !isCompleted ? View.VISIBLE : View.INVISIBLE);
        initRecyclerView(fastAdapter, headerItem);
        initClickEvents(fastAdapter);
    }

    @Override
    public void onTaskAdded(TaskItem item) {
        synchronized (this) {
            taskAdapter.add(item);
        }
    }

    @Override
    public void onTaskModified(TaskItem item) { // TODO this is a bug
        synchronized (this) {
            int index = getTaskItemIndex(item.getTaskName());
            if (index != -1) {
                //taskAdapter.set(index + 1, item);
                taskAdapter.set(index + 1, item);
            }
        }
    }

    @Override
    public void onTaskDeleted(TaskItem item) {
        synchronized (this) {
            int index = getTaskItemIndex(item.getTaskName());
            if (index != -1) { // TODO this is always false
                taskAdapter.remove(index + 1); // TODO while + 1?
            }
        }
    }

    @Override
    public void onTaskCompleted(CompletedTaskItem completedItem) {
        synchronized (this) {
            int index = getTaskItemIndex(completedItem.getTaskName());
            if (index != -1) {
                taskAdapter.remove(index + 1);
            }
            completedTaskAdapter.add(completedItem);
        }
    }

    @Override
    public void onProjectDeleted(boolean isSuccessful) {
        Toast.makeText(
                context,
                isSuccessful
                        ? R.string.view_project_toast_project_delete_success
                        : R.string.view_project_toast_project_delete_failed,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskDeleted(boolean isSuccessful, int position) {
        if (isSuccessful) {
            Toast.makeText(context, R.string.view_project_toast_task_delete_success, Toast.LENGTH_SHORT).show();
        } else {
            synchronized (this) {
                fastAdapter.notifyAdapterItemChanged(position);
            }
            Toast.makeText(context, R.string.view_project_toast_task_delete_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onHeaderItemChanged(String colorTag,
                                    int estimatedTime,
                                    int elapsedTime,
                                    float progress,
                                    boolean allTasksCompleted,
                                    boolean isCompleted) {
        synchronized (this) {
            ViewProjectHeaderItem item = headerAdapter.getAdapterItem(0);
            item.setColorTag(colorTag);
            item.setEstimatedTime(estimatedTime);
            item.setElapsedTime(elapsedTime);
            item.setProgress(progress);
            fastAdapter.notifyAdapterItemChanged(0);
            if (allTasksCompleted && !isCompleted) {
                if (!completeButton.isShown()) {
                    completeButton.show();
                }
            } else if (completeButton.isShown()) {
                completeButton.hide();
            }
        }
    }

    @Override
    public void onSubtitleChanged(Date dueDate, Date today) {
        subtitleTextView.setText(DateTimeManager.getDueDateString(context, dueDate, today));
        subtitleTextView.setTextColor(context.getColor(dueDate.compareTo(today) < 0
                ? R.color.colorError
                : android.R.color.darker_gray));
    }


    //                                                                                       HELPERS

    private void initRecyclerView(FastAdapter fastAdapter, ViewProjectHeaderItem headerItem) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setItemAnimator(new DefaultAnimator<>());

        headerAdapter.add(headerItem);

        ItemAdapter<AddTaskItem> addAdapter = new ItemAdapter<>();
        addAdapter.add(new AddTaskItem(getString(R.string.view_project_title_add_task), true));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, taskAdapter);
        fastAdapter.addAdapter(2, completedTaskAdapter);
        fastAdapter.addAdapter(3, addAdapter);

        fastAdapter.setHasStableIds(true);
        fastAdapter.withSelectable(true);
        attachSwipeHelper(recyclerView);
        recyclerView.setAdapter(fastAdapter);

    }

    private View.OnClickListener getHeaderClickListener() {
        return view -> {
            if (view.getId() == R.id.button_options) {
                showOptionsPopup();
            }
        };
    }

    private void attachSwipeHelper(RecyclerView recyclerView) {
        SimpleSwipeCallback swipeCallback = new SimpleSwipeCallback(
                this::executeSwipeAction,
                context.getDrawable(R.drawable.ic_delete_red),
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                context.getColor(android.R.color.transparent))
                .withLeaveBehindSwipeRight(context.getDrawable(R.drawable.ic_edit_black));
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void executeSwipeAction(int position, int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            presenter.editTask(taskAdapter.getAdapterItem(position - 1));
        } else if (direction == ItemTouchHelper.LEFT) {
            Set<Integer> positionSet = new HashSet<>();
            positionSet.add(position);
            undoHelper.remove(
                    recyclerView,
                    getString(R.string.view_project_toast_task_delete_success),
                    getString(R.string.core_undo),
                    LENGTH_SNACK_BAR,
                    positionSet);
        }
        fastAdapter.notifyAdapterItemChanged(position);
    }

    private void initClickEvents(FastAdapter<AbstractItem> fastAdapter) {
        View.OnClickListener onClickListener = view -> {
            int id = view.getId();
            if (id == R.id.button_close) {
                fragmentListener.onCloseFragment();
            } else if (id == R.id.button_complete) {
                // TODO implement
            }
        };
        closeButton.setOnClickListener(onClickListener);
        completeButton.setOnClickListener(onClickListener);
        fastAdapter.withOnClickListener((view, adapter, item, position) -> {
            if (view == null || !item.isEnabled()) {
                return false;
            }
            if (view.getId() == R.id.item_task) {
                presenter.selectTask(((TaskItem) item));
                return true;
            }
            if (view.getId() == R.id.item_add) {
                presenter.addTask();
                return true;
            }

            return false;
        });
    }

    private void showOptionsPopup() {

        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.showPopup(
                R.layout.popup_options_view_project,
                recyclerView,
                Gravity.TOP | Gravity.END);

        View contentView = popupWindow.getContentView();

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_edit) {
                popupWindow.dismiss();
                presenter.editProject();
            } else if (view.getId() == R.id.button_delete) {
                popupWindow.dismiss();
                fragmentListener.onCloseFragment();
                presenter.deleteProject();
            }
        };
        contentView.findViewById(R.id.button_edit).setOnClickListener(onClickListener);
        contentView.findViewById(R.id.button_delete).setOnClickListener(onClickListener);

    }

    private int getTaskItemIndex(@NonNull String name) {
        return IntStream.range(0, taskAdapter.getAdapterItems().size())
                .filter(i -> name.equals(taskAdapter.getAdapterItems().get(i).getTaskName()))
                .findFirst().orElse(-1);
    }

}

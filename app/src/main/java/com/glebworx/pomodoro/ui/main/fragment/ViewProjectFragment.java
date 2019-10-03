package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.item.ViewProjectHeaderItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;
import com.glebworx.pomodoro.util.tasks.InitTasksTask;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.itemanimators.AlphaInAnimator;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.glebworx.pomodoro.util.constants.Constants.LENGTH_SNACK_BAR;


// TODO add board and calendar view
public class ViewProjectFragment extends Fragment {

    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.text_view_subtitle) AppCompatTextView subtitleTextView;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private static final String ARG_PROJECT_MODEL = "project_model";

    //private static NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale.getDefault());

    private ProjectModel projectModel;
    private FastAdapter<AbstractItem> fastAdapter;
    private ItemAdapter<ViewProjectHeaderItem> headerAdapter;
    private ItemAdapter<TaskItem> taskAdapter;
    private UndoHelper<AbstractItem> undoHelper;
    private EventListener<QuerySnapshot> eventListener;
    private OnViewProjectFragmentInteractionListener fragmentListener;
    private InitTasksTask initTasksTask;
    private Unbinder unbinder;

    public ViewProjectFragment() { }

    public static ViewProjectFragment newInstance(ProjectModel projectModel) {
        ViewProjectFragment fragment = new ViewProjectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_project, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        Context context = getContext();
        if (arguments == null || context == null) {
            return rootView;
        }
        projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        if (projectModel == null) {
            return rootView;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        initTitle();
        initRecyclerView(context, layoutManager, fastAdapter);
        notifyHeaderItemChanged();
        initClickEvents(context, fastAdapter);

        TaskApi.addTaskEventListener(projectModel.getName(), eventListener);

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Context context = getContext();
        if (context != null && !hidden) {
            initTitle();
            notifyHeaderItemChanged();
            updateToday(context);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        headerAdapter = new ItemAdapter<>();
        taskAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        undoHelper = new UndoHelper<>(fastAdapter, (positions, removed) -> {
            for (FastAdapter.RelativeInfo<AbstractItem> relativeInfo: removed) {
                deleteTask(context, ((TaskItem) relativeInfo.item).getModel(), fastAdapter, relativeInfo.position);
            }
            /*ProjectItem item;
            for (int position: positions) {
                item = projectAdapter.getAdapterItem(position - 1);
                deleteProject(context, item.getModel(), position);
            }*/

        });

        super.onAttach(context);
        eventListener = (snapshots, e) -> {
            if (initTasksTask != null && initTasksTask.getStatus() != AsyncTask.Status.FINISHED) {
                initTasksTask.cancel(true);
            }
            if (snapshots == null) {
                return;
            }
            initTasksTask = new InitTasksTask(snapshots, taskAdapter);
            initTasksTask.execute();
        };
        fragmentListener = (OnViewProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        eventListener = null;
        fragmentListener = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        Context context = getContext();
        if (context != null) {
            updateToday(context);
        }
    }

    private void initTitle() {
        titleTextView.setText(projectModel.getName());
    }

    private void notifyHeaderItemChanged() {
        ViewProjectHeaderItem item = headerAdapter.getAdapterItem(0);
        item.setEstimatedTime(projectModel.getEstimatedTime());
        item.setElapsedTime(projectModel.getElapsedTime());
        item.setProgress(projectModel.getProgressRatio());
        fastAdapter.notifyAdapterItemChanged(0);
    }

    private void initRecyclerView(Context context, LinearLayoutManager layoutManager, FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new AlphaCrossFadeAnimator());

        headerAdapter.add(new ViewProjectHeaderItem(projectModel, view -> {
            switch (view.getId()) {
                case R.id.button_options:
                    showOptionsPopup(context);
                    break;
            }
        }));

        ItemAdapter<AddItem> addAdapter = new ItemAdapter<>();
        addAdapter.add(new AddItem(getString(R.string.view_project_title_add_task), true));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, taskAdapter);
        //fastAdapter.addAdapter(2, completedAdapter);
        fastAdapter.addAdapter(2, addAdapter);

        fastAdapter.setHasStableIds(true);
        fastAdapter.withSelectable(true);
        attachSwipeHelper(context, recyclerView);
        recyclerView.setAdapter(fastAdapter);

    }

    private void attachSwipeHelper(Context context,
                                   RecyclerView recyclerView) {
        SimpleSwipeCallback swipeCallback = new SimpleSwipeCallback(
                (position, direction) -> executeSwipeAction(context, position, direction),
                context.getDrawable(R.drawable.ic_delete_red),
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                context.getColor(android.R.color.transparent))
                .withLeaveBehindSwipeRight(context.getDrawable(R.drawable.ic_edit_black));
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void executeSwipeAction(Context context, int position, int direction) {
        TaskModel taskModel = taskAdapter.getAdapterItem(position - 1).getModel();
        if (direction == ItemTouchHelper.RIGHT) {
            fragmentListener.onEditTask(projectModel, taskModel);
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

    private void deleteTask(Context context, TaskModel taskModel, FastAdapter fastAdapter, int position) {
        TaskApi.deleteTask(projectModel, taskModel, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, R.string.view_project_toast_task_delete_success, Toast.LENGTH_SHORT).show();
            } else {
                fastAdapter.notifyAdapterItemChanged(position);
                Toast.makeText(context, R.string.view_project_toast_task_delete_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initClickEvents(Context context, FastAdapter<AbstractItem> fastAdapter) {
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_close) {
                fragmentListener.onCloseFragment();
            }
        };
        closeButton.setOnClickListener(onClickListener);
        fastAdapter.withOnClickListener((view, adapter, item, position) -> {
            if (view == null) {
                return false;
            }
            if (view.getId() == R.id.item_task && item instanceof TaskItem) {
                fragmentListener.onSelectTask(((TaskItem) item).getModel());
                return true;
            }
            if (view.getId() == R.id.item_add) {
                fragmentListener.onAddTask(projectModel);
                return true;
            }

            return false;
        });
    }

    private void showOptionsPopup(Context context) {

        PopupWindowManager popupWindowManager = new PopupWindowManager(context);
        PopupWindow popupWindow = popupWindowManager.showPopup(
                R.layout.popup_options_view_project,
                recyclerView,
                Gravity.TOP | Gravity.END);

        View contentView = popupWindow.getContentView();

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_edit) {
                popupWindow.dismiss();
                fragmentListener.onEditProject(projectModel);
            } else if (view.getId() == R.id.button_delete) {
                popupWindow.dismiss();
                deleteProject(context);
            }
        };
        contentView.findViewById(R.id.button_edit).setOnClickListener(onClickListener);
        contentView.findViewById(R.id.button_delete).setOnClickListener(onClickListener);

    }

    private void deleteProject(Context context) {
        fragmentListener.onCloseFragment();
        ProjectApi.deleteProject(projectModel, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, R.string.view_project_toast_project_delete_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.view_project_toast_project_delete_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateToday(Context context) {
        //dateTimeManager.setCurrentCalendar();
        Date newDate = new Date();
        subtitleTextView.setText(DateTimeManager.getDueDateString(context, projectModel.getDueDate(), newDate));
        if (projectModel.getDueDate().compareTo(newDate) < 0) {
            subtitleTextView.setTextColor(context.getColor(R.color.colorError));
        } else {
            subtitleTextView.setTextColor(context.getColor(android.R.color.darker_gray));
        }
    }

    public interface OnViewProjectFragmentInteractionListener {
        void onEditProject(ProjectModel projectModel);
        void onAddTask(ProjectModel projectModel);
        void onSelectTask(TaskModel taskModel);
        void onEditTask(ProjectModel projectModel, TaskModel taskModel);
        void onCloseFragment();
    }

}

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
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragment;
import com.glebworx.pomodoro.ui.fragment.view_project.interfaces.IViewProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.view_project.item.AddTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;
import com.google.firebase.firestore.DocumentChange;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.glebworx.pomodoro.util.constants.Constants.LENGTH_SNACK_BAR;


// TODO add board and calendar view
public class ViewProjectFragment extends Fragment implements IViewProjectFragment {


    //                                                                                       BINDING

    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.text_view_subtitle) AppCompatTextView subtitleTextView;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;


    //                                                                                     CONSTANTS

    static final String ARG_PROJECT_MODEL = "project_model";


    //                                                                                    ATTRIBUTES

    private Context context;
    private FastAdapter<AbstractItem> fastAdapter;
    private ItemAdapter<ViewProjectHeaderItem> headerAdapter;
    private ItemAdapter<TaskItem> taskAdapter;
    private UndoHelper<AbstractItem> undoHelper;
    private Observable<DocumentChange> observable;
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
        presenter.updateHeaderItem();
        presenter.updateSubtitle();
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
        if (!hidden) {
            presenter.updateHeaderItem();
            presenter.updateSubtitle();
        }
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

    @Override
    public void onDestroy() {
        observable.unsubscribeOn(Schedulers.io());
        super.onDestroy();
    }

    //                                                                                IMPLEMENTATION

    @Override
    public void onInitView(String projectName, ViewProjectHeaderItem headerItem) {
        headerAdapter = new ItemAdapter<>();
        taskAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        undoHelper = new UndoHelper<>(fastAdapter, (positions, removed) -> {
            for (FastAdapter.RelativeInfo<AbstractItem> relativeInfo : removed) {
                presenter.deleteTask(((TaskItem) relativeInfo.item), relativeInfo.position);
            }
        });
        titleTextView.setText(projectName);
        initRecyclerView(fastAdapter, headerItem);
        initClickEvents(fastAdapter);
        observable = TaskApi.getTaskEventObservable(projectName);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
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
            fastAdapter.notifyAdapterItemChanged(position);
            Toast.makeText(context, R.string.view_project_toast_task_delete_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onHeaderItemChanged(int estimatedTime, int elapsedTime, double progressRatio) {
        ViewProjectHeaderItem item = headerAdapter.getAdapterItem(0);
        item.setEstimatedTime(estimatedTime);
        item.setElapsedTime(elapsedTime);
        item.setProgress(progressRatio);
        fastAdapter.notifyAdapterItemChanged(0);
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
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new AlphaCrossFadeAnimator());

        headerAdapter.add(headerItem);

        ItemAdapter<AddTaskItem> addAdapter = new ItemAdapter<>();
        addAdapter.add(new AddTaskItem(getString(R.string.view_project_title_add_task), true));

        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, taskAdapter);
        //fastAdapter.addAdapter(2, completedAdapter);
        fastAdapter.addAdapter(2, addAdapter);

        fastAdapter.setHasStableIds(true);
        fastAdapter.withSelectable(true);
        attachSwipeHelper(recyclerView);
        recyclerView.setAdapter(fastAdapter);

    }

    private View.OnClickListener getHeaderClickListener() {
        return view -> {
            switch (view.getId()) {
                case R.id.button_options:
                    showOptionsPopup();
                    break;
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

    private io.reactivex.Observer<DocumentChange> getObserver() {
        return new io.reactivex.Observer<DocumentChange>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(DocumentChange documentChange) {
                TaskItem item = new TaskItem(documentChange.getDocument().toObject(TaskModel.class));
                int index;
                switch (documentChange.getType()) {
                    case ADDED:
                        taskAdapter.add(item);
                        break;
                    case MODIFIED:
                        index = getTaskItemIndex(item.getTaskName());
                        if (index != -1) {
                            taskAdapter.set(index + 1, item);
                            //itemAdapter.set(getTaskItemIndex(item.getTaskName()), item);
                        }
                        break;
                    case REMOVED:
                        index = getTaskItemIndex(item.getTaskName());
                        if (index != -1) {
                            taskAdapter.remove(index + 1);
                        }
                        break;
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private int getTaskItemIndex(@NonNull String name) {
        return IntStream.range(0, taskAdapter.getAdapterItems().size())
                .filter(i -> name.equals(taskAdapter.getAdapterItems().get(i).getTaskName()))
                .findFirst().orElse(-1);
    }

}

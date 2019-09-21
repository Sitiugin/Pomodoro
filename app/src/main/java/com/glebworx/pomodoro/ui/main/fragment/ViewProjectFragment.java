package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.PopupWindowCompat;
import androidx.fragment.app.Fragment;
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
import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.DummyDataProvider;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.constants.Constants;
import com.glebworx.pomodoro.util.manager.ColorManager;
import com.glebworx.pomodoro.util.manager.PopupWindowManager;
import com.glebworx.pomodoro.util.tasks.InitTasksTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewProjectFragment extends Fragment {

    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.text_view_subtitle) AppCompatTextView subtitleTextView;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private static final String ARG_PROJECT_MODEL = "project_model";

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());

    private ProjectModel projectModel;
    private ItemAdapter<TaskItem> taskAdapter;
    private EventListener<QuerySnapshot> eventListener;
    private OnViewProjectFragmentInteractionListener fragmentListener;
    private InitTasksTask initTasksTask;

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
        ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        Context context = getContext();
        if (arguments == null || context == null) {
            return rootView;
        }
        projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        if (projectModel == null) {
            return rootView;
        }

        FastAdapter<AbstractItem> fastAdapter = new FastAdapter<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        initTitle(context);
        initRecyclerView(layoutManager, fastAdapter);
        initClickEvents(context, fastAdapter);

        TaskApi.addModelEventListener(projectModel.getName(), eventListener);

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        taskAdapter = new ItemAdapter<>();
        if (initTasksTask != null && initTasksTask.getStatus() != AsyncTask.Status.FINISHED) {
            initTasksTask.cancel(true);
        }
        super.onAttach(context);
        eventListener = (snapshots, e) -> {
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

    private void initTitle(Context context) {
        titleTextView.setText(projectModel.getName());
        titleTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ColorManager.getDrawable(context, projectModel.getColorTag()), null);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(projectModel.getDueDate());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        subtitleTextView.setText(getString(R.string.core_due, dateFormat.format(calendar.getTime())));
        /*Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        YearMonth yearMonthObject = YearMonth.of(year, month + 1);
        if (year == this.year && monthOfYear == this.month) {
            if (dayOfMonth == this.today) {
                subtitleTextView.setText(R.string.core_today);
            } else if (dayOfMonth == this.today + 1 || dayOfMonth == 1 && this.today == yearMonthObject.lengthOfMonth()) {
                subtitleTextView.setText(R.string.core_tomorrow);
            } else {
                dueDateButton.setText(dateFormat.format(calendar.getTime()));
            }
        } else {
            subtitleTextView.setText(getString(R.string.core_due, dateFormat.format(calendar.getTime())));
        }*/
    }

    private void initRecyclerView(LinearLayoutManager layoutManager, FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty));
        recyclerView.setItemAnimator(new SlideInOutLeftAnimator(recyclerView));

        ItemAdapter<ProjectHeaderItem> headerAdapter = new ItemAdapter<>();
        headerAdapter.add(new ProjectHeaderItem());
        ItemAdapter<AddItem> addAdapter = new ItemAdapter<>();
        addAdapter.add(new AddItem(getString(R.string.view_project_title_add_task)));

        fastAdapter.addAdapter(0, taskAdapter);
        fastAdapter.addAdapter(1, addAdapter);

        fastAdapter.setHasStableIds(true);
        fastAdapter.withSelectable(true);
        recyclerView.setAdapter(fastAdapter);

    }

    private void initClickEvents(Context context, FastAdapter<AbstractItem> fastAdapter) {
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_close) {
                fragmentListener.onCloseFragment();
            } else if (view.getId() == R.id.button_options) {
                showOptionsPopup(context);
            }
        };
        optionsButton.setOnClickListener(onClickListener);
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
                R.layout.popup_options_project,
                optionsButton,
                Gravity.BOTTOM | Gravity.END);

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
                Toast.makeText(context, R.string.view_project_toast_delete_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.view_project_toast_delete_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface OnViewProjectFragmentInteractionListener {
        void onEditProject(ProjectModel projectModel);
        void onAddTask(ProjectModel projectModel);
        void onSelectTask(TaskModel taskModel);
        void onCloseFragment();
    }

}

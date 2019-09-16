package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.item.AddItem;
import com.glebworx.pomodoro.item.ProjectHeaderItem;
import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.DummyDataProvider;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.constants.Constants;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewProjectFragment extends Fragment {

    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.text_view_subtitle) AppCompatTextView subtitleTextView;
    @BindView(R.id.button_options) AppCompatImageButton optionsButton;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private static final String ARG_PROJECT_MODEL = "project_model";

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());

    private ItemAdapter<TaskItem> taskAdapter;
    private OnViewProjectFragmentInteractionListener fragmentListener;

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
        ProjectModel projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        if (projectModel == null) {
            return rootView;
        }

        taskAdapter = new ItemAdapter<>();
        taskAdapter.add(DummyDataProvider.getTasks());
        FastAdapter<AbstractItem> fastAdapter = new FastAdapter<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);

        initTitle(projectModel);
        initRecyclerView(layoutManager, fastAdapter);
        initClickEvents(fastAdapter);

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnViewProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }

    private void initTitle(ProjectModel projectModel) {
        titleTextView.setText(projectModel.getName());
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
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

    private void initClickEvents(FastAdapter<AbstractItem> fastAdapter) {
        fastAdapter.withOnClickListener((view, adapter, item, position) -> {
            if (view == null) {
                return false;
            }
            if (view.getId() == R.id.item_task && item instanceof TaskItem) {
                fragmentListener.onSelectTask(((TaskItem) item).getModel());
                return true;
            }
            if (view.getId() == R.id.item_add) {
                fragmentListener.onAddTask();
                return true;
            }
            return false;
        });
    }

    public interface OnViewProjectFragmentInteractionListener {
        void onAddTask();
        void onSelectTask(TaskModel taskModel);
    }

}

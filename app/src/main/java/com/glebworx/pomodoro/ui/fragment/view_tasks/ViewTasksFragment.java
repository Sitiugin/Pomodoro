package com.glebworx.pomodoro.ui.fragment.view_tasks;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.view_project.item.CompletedTaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.ViewProjectHeaderItem;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragment;
import com.glebworx.pomodoro.ui.fragment.view_tasks.interfaces.IViewTasksFragmentInteractionListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ViewTasksFragment extends Fragment implements IViewTasksFragment {


    //                                                                                       BINDING

    public static final String TYPE_TODAY = "today";
    public static final String TYPE_THIS_WEEK = "this_week";
    public static final String TYPE_OVERDUE = "overdue";


    //                                                                                     CONSTANTS
    private static final String ARG_TYPE = "type";
    @BindView(R.id.text_view_subtitle)
    AppCompatTextView subtitleTextView;
    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    //                                                                                    ATTRIBUTES
    private Context context;
    private IViewTasksFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ViewTasksFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public ViewTasksFragment() {
    }


    //                                                                                       FACTORY

    public static ViewTasksFragment newInstance(String type) {
        ViewTasksFragment fragment = new ViewTasksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_tasks, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ViewTasksFragmentPresenter(
                this,
                fragmentListener,
                getArguments());
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
        fragmentListener = (IViewTasksFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void onInitView(String projectName, ViewProjectHeaderItem headerItem) {

    }

    @Override
    public void onTaskAdded(TaskItem item) {

    }

    @Override
    public void onTaskModified(TaskItem item) {

    }

    @Override
    public void onTaskDeleted(TaskItem item) {

    }

    @Override
    public void onTaskCompleted(TaskItem item, CompletedTaskItem completedItem) {

    }

    @Override
    public void onTaskDeleted(boolean isSuccessful, int position) {

    }

    @Override
    public void onHeaderItemChanged(int estimatedTime, int elapsedTime, double progressRatio) {

    }

    @Override
    public void onSubtitleChanged(Date dueDate, Date today) {

    }

}

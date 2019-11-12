package com.glebworx.pomodoro.ui.fragment.add_task;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragment;
import com.glebworx.pomodoro.ui.fragment.add_task.interfaces.IAddTaskFragmentInteractionListener;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.KeyboardManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class AddTaskFragment extends Fragment implements IAddTaskFragment {


    //                                                                                       BINDING

    @BindView(R.id.layout_add_task) ConstraintLayout addTaskLayout;
    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.edit_text_name) AppCompatEditText taskNameEditText;
    @BindView(R.id.text_view_section_name) AppCompatTextView taskNameSectionTextView;
    @BindView(R.id.button_due_date) AppCompatButton dueDateButton;
    @BindView(R.id.button_pomodoros_allocated)
    AppCompatButton pomodorosAllocatedButton;
    @BindView(R.id.button_save) ExtendedFloatingActionButton saveButton;
    @BindView(R.id.spin_kit_view) SpinKitView spinKitView;


    //                                                                                     CONSTANTS

    public static final String ARG_PROJECT_MODEL = "project_model";
    public static final String ARG_TASK_MODEL = "task_model";


    //                                                                                    ATTRIBUTES

    private IAddTaskFragmentInteractionListener fragmentListener;
    private Activity activity;
    private Context context;
    private ConstraintSet constraintSet;
    private Unbinder unbinder;
    private AddTaskFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public AddTaskFragment() { }


    //                                                                                       FACTORY

    public static AddTaskFragment newInstance(ProjectModel projectModel) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, new ProjectModel(projectModel));
        fragment.setArguments(args);
        return fragment;
    }

    public static AddTaskFragment newInstance(ProjectModel projectModel, TaskModel taskModel) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, new ProjectModel(projectModel));
        args.putParcelable(ARG_TASK_MODEL, new TaskModel(taskModel));
        fragment.setArguments(args);
        return fragment;
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);
        activity = getActivity();
        context = getContext();
        if (activity == null || context == null) {
            fragmentListener.onCloseFragment();
        }
        constraintSet = new ConstraintSet();
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new AddTaskFragmentPresenter(this, getArguments());
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
        fragmentListener = (IAddTaskFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                IMPLEMENTATION

    @Override
    public void onInitView(boolean isEditing,
                           String taskName,
                           String dueDate,
                           int pomodorosAllocated) {

        dueDateButton.setText(dueDate);
        pomodorosAllocatedButton.setText(getString(
                pomodorosAllocated == 1 ? R.string.core_pomodoro : R.string.core_pomodoros,
                String.valueOf(pomodorosAllocated)));

        if (isEditing) {
            taskNameEditText.setVisibility(View.GONE);
            taskNameSectionTextView.setVisibility(View.GONE);
            titleTextView.setText(context.getString(R.string.core_edit_something, taskName));
            saveButton.setText(R.string.add_task_title_update_task);
        } else {
            initEditText();
        }

        initClickEvents();

    }

    @Override
    public void onTaskNameChanged() {
        KeyboardManager.hideKeyboard(activity);
        taskNameEditText.clearFocus();
    }

    @Override
    public void onEditDueDate(Date dueDate) {
        updateName();
        showDatePickerDialog(dueDate);
    }

    @Override
    public void onEditPomodorosAllocated(int pomodorosAllocated) {
        AlertDialog alertDialog = DialogManager.showDialog(
                activity,
                R.id.container_main,
                R.layout.dialog_pomodoro_picker);
        NumberPicker picker = alertDialog.findViewById(R.id.number_picker);
        picker.setMinValue(1);
        picker.setMaxValue(25);
        picker.setWrapSelectorWheel(true);
        picker.setFormatter(value -> {
            if (value == 1) {
                return getString(R.string.core_pomodoro, String.valueOf(value));
            }
            return getString(R.string.core_pomodoros, String.valueOf(value));
        });
        picker.setValue(pomodorosAllocated);

        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_positive) {
                presenter.selectPomodorosAllocated(picker.getValue());
                alertDialog.dismiss();
            } else if (view.getId() == R.id.button_negative) {
                alertDialog.dismiss();
            }
        };

        alertDialog.findViewById(R.id.button_positive).setOnClickListener(onClickListener);
        alertDialog.findViewById(R.id.button_negative).setOnClickListener(onClickListener);

    }

    @Override
    public void onPomodorosChanged(int pomodorosAllocated) {
        pomodorosAllocatedButton.setText(getString(
                pomodorosAllocated == 1 ? R.string.core_pomodoro : R.string.core_pomodoros,
                String.valueOf(pomodorosAllocated)));
    }

    @Override
    public void onSelectDueDate(String dateString) {
        dueDateButton.setText(dateString);
    }

    @Override
    public void onAddTaskStart() {
        startSaveStartedAnimation();
    }

    @Override
    public void onAddTaskSuccess(boolean isEditing) {
        Toast.makeText(
                context,
                isEditing ? R.string.add_task_toast_update_success : R.string.add_task_toast_add_success,
                Toast.LENGTH_SHORT).show();
        fragmentListener.onCloseFragment();
    }

    @Override
    public void onAddTaskFailure(boolean isEditing) {
        Toast.makeText(
                context,
                isEditing ? R.string.add_task_toast_update_failed : R.string.add_task_toast_add_failed,
                Toast.LENGTH_LONG).show();
        startSaveCanceledAnimation();
    }

    @Override
    public void onTaskValidationFailed(boolean isEmpty) {
        Toast.makeText(
                context,
                isEmpty ? R.string.add_task_err_name_empty : R.string.add_task_err_name_invalid,
                Toast.LENGTH_LONG).show();
    }


    //                                                                                       HELPERS

    private void initEditText() {
        taskNameEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateName();
            }
            return false;
        });
        taskNameEditText.requestFocus();
        KeyboardManager.showKeyboard(activity, taskNameEditText);
    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            KeyboardManager.hideKeyboard(activity);
            switch (view.getId()) {
                case R.id.button_due_date:
                    presenter.editDueDate();
                    break;
                case R.id.button_pomodoros_allocated:
                    presenter.editPomodorosAllocated();
                    break;
                case R.id.button_save:
                    updateName();
                    presenter.saveTask();
                    break;
                case R.id.button_close:
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        dueDateButton.setOnClickListener(onClickListener);
        pomodorosAllocatedButton.setOnClickListener(onClickListener);
        saveButton.setOnClickListener(onClickListener);
        closeButton.setOnClickListener(onClickListener);
    }

    private void showDatePickerDialog(Date dueDate) {
        AlertDialog alertDialog = DialogManager.showDialog(
                activity,
                R.id.container_main,
                R.layout.dialog_date_picker);
        DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(dueDate);
        Objects.requireNonNull(datePicker).setMinDate(System.currentTimeMillis() - 1000);
        datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                getDateChangeListener(alertDialog));
    }

    private DatePicker.OnDateChangedListener getDateChangeListener(AlertDialog alertDialog) {
        return (view, year, monthOfYear, dayOfMonth) -> {
            presenter.selectDueDate(year, monthOfYear, dayOfMonth);
            alertDialog.dismiss();
        };
    }

    private void startSaveStartedAnimation() {
        saveButton.setEnabled(false);
        TransitionManager.endTransitions(addTaskLayout);
        TransitionManager.beginDelayedTransition(addTaskLayout);
        constraintSet.clone(addTaskLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.INVISIBLE);
        constraintSet.applyTo(addTaskLayout);
    }

    private void startSaveCanceledAnimation() {
        saveButton.setEnabled(true);
        TransitionManager.endTransitions(addTaskLayout);
        TransitionManager.beginDelayedTransition(addTaskLayout);
        constraintSet.clone(addTaskLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.VISIBLE);
        constraintSet.applyTo(addTaskLayout);
    }

    private void updateName() {
        presenter.editTaskName(Objects.requireNonNull(taskNameEditText.getText()).toString().trim());
    }

}

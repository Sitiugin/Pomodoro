package com.glebworx.pomodoro.ui.fragment.add_task;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
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

import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_DAY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_FIVE_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_FOUR_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_SIX_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_THREE_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_TWO_DAYS;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_EVERY_WEEKLY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_MONTHLY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_WEEKDAY;
import static com.glebworx.pomodoro.model.TaskModel.RECURRENCE_WEEKEND;


public class AddTaskFragment extends Fragment implements IAddTaskFragment {


    //                                                                                       BINDING

    @BindView(R.id.layout_add_task) ConstraintLayout addTaskLayout;
    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.edit_text_name) AppCompatEditText taskNameEditText;
    @BindView(R.id.text_view_section_name) AppCompatTextView taskNameSectionTextView;
    @BindView(R.id.button_due_date) AppCompatButton dueDateButton;
    @BindView(R.id.spinner_pomodoros_allocated) AppCompatSpinner allocatedTimeSpinner;
    @BindView(R.id.spinner_recurrence) AppCompatSpinner recurrenceSpinner;
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
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddTaskFragment newInstance(ProjectModel projectModel, TaskModel taskModel) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        args.putParcelable(ARG_TASK_MODEL, taskModel);
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
                           int pomodorosAllocated,
                           String recurrence) {

        if (isEditing) {
            taskNameEditText.setVisibility(View.GONE);
            taskNameSectionTextView.setVisibility(View.GONE);
            titleTextView.setText(context.getString(R.string.core_edit_something, taskName));
            dueDateButton.setText(dueDate);
            allocatedTimeSpinner.setSelection(pomodorosAllocated - 1, true);
            selectRecurrence(recurrence);
            saveButton.setText(R.string.add_task_title_update_task);
        } else {
            dueDateButton.setText(dueDate);
            initEditText();
        }

        initSpinners();
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

    private void initSpinners() {
        allocatedTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                presenter.selectPomodorosAllocated(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        allocatedTimeSpinner.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                updateName();
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                return true;
            }
            return false;
        });
        recurrenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                presenter.selectRecurrence(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }

        });
        recurrenceSpinner.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                updateName();
                return true;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.performClick();
                return true;
            }
            return false;
        });
    }

    private void selectRecurrence(String recurrenceString) {
        if (recurrenceString == null) {
            return;
        }
        switch (recurrenceString) {
            case RECURRENCE_EVERY_DAY:
                recurrenceSpinner.setSelection(1, true);
                break;
            case RECURRENCE_EVERY_TWO_DAYS:
                recurrenceSpinner.setSelection(2, true);
                break;
            case RECURRENCE_EVERY_THREE_DAYS:
                recurrenceSpinner.setSelection(3, true);
                break;
            case RECURRENCE_EVERY_FOUR_DAYS:
                recurrenceSpinner.setSelection(4, true);
                break;
            case RECURRENCE_EVERY_FIVE_DAYS:
                recurrenceSpinner.setSelection(5, true);
                break;
            case RECURRENCE_EVERY_SIX_DAYS:
                recurrenceSpinner.setSelection(6, true);
                break;
            case RECURRENCE_EVERY_WEEKLY:
                recurrenceSpinner.setSelection(7, true);
                break;
            case RECURRENCE_WEEKDAY:
                recurrenceSpinner.setSelection(8, true);
                break;
            case RECURRENCE_WEEKEND:
                recurrenceSpinner.setSelection(9, true);
                break;
            case RECURRENCE_MONTHLY:
                recurrenceSpinner.setSelection(10, true);
                break;
        }
    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_due_date:
                    presenter.editDueDate();
                    break;
                case R.id.button_save:
                    updateName();
                    presenter.addTask();
                    break;
                case R.id.button_close:
                    KeyboardManager.hideKeyboard(activity);
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        dueDateButton.setOnClickListener(onClickListener);
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

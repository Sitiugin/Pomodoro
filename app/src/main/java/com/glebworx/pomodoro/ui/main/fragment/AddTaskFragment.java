package com.glebworx.pomodoro.ui.main.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

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

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.constants.Constants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.KeyboardManager;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

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


public class AddTaskFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.layout_add_task) ConstraintLayout addTaskLayout;
    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.edit_text_name) AppCompatEditText taskNameEditText;
    @BindView(R.id.text_view_section_name) AppCompatTextView taskNameSectionTextView;
    @BindView(R.id.button_due_date) AppCompatButton dueDateButton;
    @BindView(R.id.spinner_pomodoros_allocated) AppCompatSpinner allocatedTimeSpinner;
    @BindView(R.id.spinner_recurrence) AppCompatSpinner recurrenceSpinner;
    @BindView(R.id.button_save) AppCompatButton saveButton;
    @BindView(R.id.spin_kit_view) SpinKitView spinKitView;


    //                                                                                     CONSTANTS

    private static final String ARG_PROJECT_MODEL = "project_model";
    private static final String ARG_TASK_MODEL = "task_model";

    //                                                                                    ATTRIBUTES

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());
    private OnAddTaskFragmentInteractionListener fragmentListener;
    private ConstraintSet constraintSet;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private Calendar currentCalendar;
    private Calendar targetCalendar;
    private boolean isEditing;


    //                                                                                  CONSTRUCTORS

    public AddTaskFragment() { }


    //                                                                                     LIFECYCLE

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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, rootView);

        currentCalendar = Calendar.getInstance(Locale.getDefault());
        DateTimeManager.clearTime(currentCalendar);
        targetCalendar = Calendar.getInstance(Locale.getDefault());
        DateTimeManager.clearTime(targetCalendar);

        constraintSet = new ConstraintSet();

        Activity activity = getActivity();
        Context context = getContext();
        if (activity == null || context == null) {
            return rootView;
        }

        updateToday(context);

        Bundle arguments = getArguments();
        if (arguments == null) {
            return rootView;
        }
        projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        if (projectModel == null) {
            projectModel = new ProjectModel();
            return rootView;
        }
        taskModel = arguments.getParcelable(ARG_TASK_MODEL);
        if (taskModel == null) {
            taskModel = new TaskModel();
            isEditing = false;
        } else {
            isEditing = true;
        }

        if (isEditing) {
            taskNameEditText.setVisibility(View.GONE);
            taskNameSectionTextView.setVisibility(View.GONE);
            titleTextView.setText(context.getString(R.string.core_edit_something, taskModel.getName()));
            targetCalendar.setTime(taskModel.getDueDate());
            DateTimeManager.clearTime(targetCalendar);
            dueDateButton.setText(DateTimeManager.getDateString(context, currentCalendar, targetCalendar, dateFormat));
            allocatedTimeSpinner.setSelection(taskModel.getPomodorosAllocated() - 1, true);
            selectRecurrence(taskModel.getRecurrence());
            saveButton.setText(R.string.add_task_title_update_task);
        } else {
            DateTimeManager.clearTime(targetCalendar);
            taskModel.setDueDate(targetCalendar.getTime());
            dueDateButton.setText(getString(R.string.core_today));
            taskModel.setPomodorosAllocated(1);
            initEditText(activity);
        }

        initSpinners(activity);
        initClickEvents(activity);

        return rootView;

    }

    @Override
    public void onResume() {
        Context context = getContext();
        if (context != null) {
            updateToday(context);
        }
        super.onResume();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnAddTaskFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }

    private void initEditText(Activity activity) {
        taskNameEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearEditTextFocus(activity);
            }
            return false;
        });
        taskNameEditText.requestFocus();
        KeyboardManager.showKeyboard(activity, taskNameEditText);
    }

    private void initSpinners(Activity activity) {
        allocatedTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                taskModel.setPomodorosAllocated(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        allocatedTimeSpinner.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                clearEditTextFocus(activity);
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
                switch (position) {
                    case 0:
                        taskModel.setRecurrence(null);
                        break;
                    case 1:
                        taskModel.setRecurrence(RECURRENCE_EVERY_DAY);
                        break;
                    case 2:
                        taskModel.setRecurrence(RECURRENCE_EVERY_TWO_DAYS);
                        break;
                    case 3:
                        taskModel.setRecurrence(RECURRENCE_EVERY_THREE_DAYS);
                        break;
                    case 4:
                        taskModel.setRecurrence(RECURRENCE_EVERY_FOUR_DAYS);
                        break;
                    case 5:
                        taskModel.setRecurrence(RECURRENCE_EVERY_FIVE_DAYS);
                        break;
                    case 6:
                        taskModel.setRecurrence(RECURRENCE_EVERY_SIX_DAYS);
                        break;
                    case 7:
                        taskModel.setRecurrence(RECURRENCE_EVERY_WEEKLY);
                        break;
                    case 8:
                        taskModel.setRecurrence(RECURRENCE_WEEKDAY);
                        break;
                    case 9:
                        taskModel.setRecurrence(RECURRENCE_WEEKEND);
                        break;
                    case 10:
                        taskModel.setRecurrence(RECURRENCE_MONTHLY);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        recurrenceSpinner.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                clearEditTextFocus(activity);
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

    private void initClickEvents(Activity activity) {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_due_date:
                    clearEditTextFocus(activity);
                    showDatePickerDialog(activity);
                    break;
                case R.id.button_save:
                    clearEditTextFocus(activity);
                    Context context = getContext();
                    if (!validateInput(context)) {
                        break;
                    }
                    if (taskModel.isValid()) {
                        saveTask(context);
                    } else if (context != null) {
                        Toast.makeText(
                                context,
                                isEditing ? R.string.add_task_toast_update_failed : R.string.add_task_toast_add_failed,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.button_close:
                    clearEditTextFocus(activity);
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        dueDateButton.setOnClickListener(onClickListener);
        saveButton.setOnClickListener(onClickListener);
        closeButton.setOnClickListener(onClickListener);
    }

    private boolean validateInput(Context context) {
        if (isEditing) {
            return true;
        }
        if (Objects.requireNonNull(taskNameEditText.getText()).toString().trim().isEmpty()) {
            Toast.makeText(context, R.string.add_task_err_name_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showDatePickerDialog(Activity activity) {
        AlertDialog alertDialog = DialogManager.showDialog(activity, R.id.container_main, R.layout.dialog_date_picker);
        DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        if (datePicker != null) {
            datePicker.init(
                    targetCalendar.get(Calendar.YEAR),
                    targetCalendar.get(Calendar.MONTH),
                    targetCalendar.get(Calendar.DAY_OF_MONTH),
                    getDateChangeListener(activity, alertDialog));
        }
    }

    private DatePicker.OnDateChangedListener getDateChangeListener(Context context, AlertDialog alertDialog) {
        return (view, year, monthOfYear, dayOfMonth) -> {
            targetCalendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            taskModel.setDueDate(targetCalendar.getTime());
            dueDateButton.setText(DateTimeManager.getDateString(context, currentCalendar, targetCalendar, dateFormat));
            alertDialog.dismiss();
        };
    }

    private void saveTask(Context context) {
        startSaveStartedAnimation();
        projectModel.addTask(taskModel);
        TaskApi.addTask(projectModel, taskModel, task -> {
            if (context == null) {
                startSaveCanceledAnimation();
                return;
            }
            if (task.isSuccessful()) {
                Toast.makeText(
                        context,
                        isEditing ? R.string.add_task_toast_update_success : R.string.add_task_toast_add_success,
                        Toast.LENGTH_SHORT).show();
                fragmentListener.onCloseFragment();
            } else {
                Toast.makeText(
                        context,
                        isEditing ? R.string.add_task_toast_update_failed : R.string.add_task_toast_add_failed,
                        Toast.LENGTH_LONG).show();
                startSaveCanceledAnimation();
            }
        });
    }

    private void startSaveStartedAnimation() {
        saveButton.setEnabled(false);
        TransitionManager.beginDelayedTransition(addTaskLayout);
        constraintSet.clone(addTaskLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.INVISIBLE);
        constraintSet.applyTo(addTaskLayout);
    }

    private void startSaveCanceledAnimation() {
        saveButton.setEnabled(true);
        TransitionManager.beginDelayedTransition(addTaskLayout);
        constraintSet.clone(addTaskLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.VISIBLE);
        constraintSet.applyTo(addTaskLayout);
    }

    private void updateToday(Context context) {
        currentCalendar = Calendar.getInstance(Locale.getDefault());
        DateTimeManager.clearTime(currentCalendar);
        dueDateButton.setText(DateTimeManager.getDateString(context, currentCalendar, targetCalendar, dateFormat));
    }

    private void clearEditTextFocus(Activity activity) {
        if (!isEditing) {
            taskModel.setName(Objects.requireNonNull(taskNameEditText.getText()).toString().trim());
            KeyboardManager.hideKeyboard(activity);
            taskNameEditText.clearFocus();
        }
    }

    public interface OnAddTaskFragmentInteractionListener {
        void onCloseFragment();
    }

}

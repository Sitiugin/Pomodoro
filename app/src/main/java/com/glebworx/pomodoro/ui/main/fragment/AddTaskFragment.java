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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.api.TaskApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.util.constants.Constants;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.KeyboardManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddTaskFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.layout_add_task) ConstraintLayout addTaskLayout;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.edit_text_name) AppCompatEditText taskNameEditText;
    @BindView(R.id.button_due_date) AppCompatButton dueDateButton;
    @BindView(R.id.spinner_pomodoros_allocated) AppCompatSpinner allocatedTimeSpinner;
    @BindView(R.id.spinner_recurrence) AppCompatSpinner recurrenceSpinner;
    @BindView(R.id.button_save) AppCompatButton saveButton;
    @BindView(R.id.spin_kit_view) SpinKitView spinKitView;

    private static final String ARG_PROJECT_MODEL = "project_model";

    //                                                                                    ATTRIBUTES

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());
    private OnAddTaskFragmentInteractionListener fragmentListener;
    private ConstraintSet constraintSet;
    private ProjectModel projectModel;
    private TaskModel taskModel;
    private Calendar calendar;
    private int year;
    private int month;
    private int today;


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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, rootView);

        constraintSet = new ConstraintSet();
        taskModel = new TaskModel();
        calendar = Calendar.getInstance(Locale.getDefault());
        updateToday();

        Activity activity = getActivity();
        Bundle arguments = getArguments();
        if (activity == null || arguments == null) {
            return rootView;
        }
        projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        if (projectModel == null) {
            return rootView;
        }

        initEditText(activity);
        initSpinners();
        initClickEvents(activity);

        return rootView;

    }

    @Override
    public void onResume() {
        updateToday();
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

    private void initSpinners() {
        taskModel.setPomodorosAllocated(1);
        allocatedTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                taskModel.setPomodorosAllocated(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        recurrenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        taskModel.setRecurrence(null);
                        break;
                    case 1:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_EVERY_DAY);
                        break;
                    case 2:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_EVERY_TWO_DAYS);
                        break;
                    case 3:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_EVERY_THREE_DAYS);
                        break;
                    case 4:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_EVERY_FOUR_DAYS);
                        break;
                    case 5:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_EVERY_FIVE_DAYS);
                        break;
                    case 6:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_EVERY_SIX_DAYS);
                        break;
                    case 7:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_EVERY_WEEKLY);
                        break;
                    case 8:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_WEEKDAY);
                        break;
                    case 9:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_WEEKEND);
                        break;
                    case 10:
                        taskModel.setRecurrence(TaskModel.RECURRENCE_MONTHLY);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
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
                        saveTasks(context);
                    } else if (context != null) {
                        Toast.makeText(context, R.string.add_task_toast_add_failed, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.button_close:
                    clearEditTextFocus(activity);
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        taskModel.setDueDate(calendar.getTime());
        dueDateButton.setText(getString(R.string.core_today));
        dueDateButton.setOnClickListener(onClickListener);
        saveButton.setOnClickListener(onClickListener);
        closeButton.setOnClickListener(onClickListener);
    }

    private boolean validateInput(Context context) {
        if (Objects.requireNonNull(taskNameEditText.getText()).toString().trim().isEmpty()) {
            Toast.makeText(context, R.string.add_project_err_name_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showDatePickerDialog(Activity activity) {
        AlertDialog alertDialog = DialogManager.showDialog(activity, R.id.container_main, R.layout.dialog_date_picker);
        DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        if (datePicker != null) {
            datePicker.init(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    getDateChangeListener(alertDialog));
        }
    }

    private DatePicker.OnDateChangedListener getDateChangeListener(AlertDialog alertDialog) {
        return (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
            YearMonth yearMonthObject = YearMonth.of(year, monthOfYear + 1);
            if (year == this.year && monthOfYear == this.month) {
                if (dayOfMonth == this.today) {
                    dueDateButton.setText(R.string.core_today);
                } else if (dayOfMonth == this.today + 1 || dayOfMonth == 1 && this.today == yearMonthObject.lengthOfMonth()) {
                    dueDateButton.setText(R.string.core_tomorrow);
                } else {
                    dueDateButton.setText(dateFormat.format(calendar.getTime()));
                }
            } else {
                dueDateButton.setText(dateFormat.format(calendar.getTime()));
            }
            alertDialog.dismiss();
        };
    }

    private void saveTasks(Context context) {
        startSaveStartedAnimation();
        projectModel.addTask(taskModel);
        TaskApi.addTask(projectModel, taskModel, task -> {
            if (context == null) {
                startSaveCanceledAnimation();
                return;
            }
            if (task.isSuccessful()) {
                Toast.makeText(context, R.string.add_task_toast_add_success, Toast.LENGTH_SHORT).show();
                fragmentListener.onCloseFragment();
            } else {
                Toast.makeText(context, R.string.add_task_toast_add_failed, Toast.LENGTH_LONG).show();
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

    private void updateToday() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.today = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void clearEditTextFocus(Activity activity) {
        taskModel.setName(Objects.requireNonNull(taskNameEditText.getText()).toString().trim());
        KeyboardManager.hideKeyboard(activity);
        taskNameEditText.clearFocus();
    }

    public interface OnAddTaskFragmentInteractionListener {
        void onCloseFragment();
    }

}

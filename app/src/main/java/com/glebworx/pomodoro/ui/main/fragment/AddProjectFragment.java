package com.glebworx.pomodoro.ui.main.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.constants.Constants;
import com.glebworx.pomodoro.util.manager.DateTimeManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.KeyboardManager;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_AMBER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_BLUE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_CYAN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_DEEP_ORANGE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_DEEP_PURPLE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_GREEN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_INDIGO_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_LIGHT_BLUE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_LIGHT_GREEN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_LIME_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_ORANGE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_PINK_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_PURPLE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_RED_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_TEAL_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_YELLOW_HEX;


public class AddProjectFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.layout_add_project) ConstraintLayout addProjectLayout;
    @BindView(R.id.text_view_title) AppCompatTextView titleTextView;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.text_view_section_name) AppCompatTextView projectNameSectionTextView;
    @BindView(R.id.edit_text_name) AppCompatEditText projectNameEditText;
    @BindView(R.id.chip_group_color) ChipGroup colorTagChipGroup;
    @BindView(R.id.button_due_date) AppCompatButton dueDateButton;
    @BindView(R.id.button_save) ExtendedFloatingActionButton saveButton;
    @BindView(R.id.spin_kit_view) SpinKitView spinKitView;


    //                                                                                     CONSTANTS

    private static final String ARG_PROJECT_MODEL = "project_model";


    //                                                                                    ATTRIBUTES

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());
    private OnAddProjectFragmentInteractionListener fragmentListener;
    private ConstraintSet constraintSet;
    private ProjectModel projectModel;
    //private Calendar targetCalendar;
    //private DateTimeManager dateTimeManager;
    private boolean isEditing;


    //                                                                                  CONSTRUCTORS

    public AddProjectFragment() { }


    //                                                                                     LIFECYCLE

    public static AddProjectFragment newInstance() {
        return new AddProjectFragment();
    }

    public static AddProjectFragment newInstance(ProjectModel projectModel) {
        AddProjectFragment fragment = new AddProjectFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PROJECT_MODEL, projectModel);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_project, container, false);
        ButterKnife.bind(this, rootView);

        constraintSet = new ConstraintSet();

        Activity activity = getActivity();
        Context context = getContext();
        if (activity == null || context == null) {
            return rootView;
        }

        //targetCalendar = Calendar.getInstance(Locale.getDefault());
        //DateTimeManager.clearTime(targetCalendar);
        //dateTimeManager = new DateTimeManager(context, targetCalendar);

        Bundle arguments = getArguments();
        if (arguments != null) {
            projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        }
        if (projectModel == null) {
            projectModel = new ProjectModel();
            isEditing = false;
        } else {
            isEditing = true;
        }

        if (isEditing) {
            projectNameEditText.setVisibility(View.GONE);
            projectNameSectionTextView.setVisibility(View.GONE);
            titleTextView.setText(context.getString(R.string.core_edit_something, projectModel.getName()));
            checkColorTag(projectModel.getColorTag());
            //targetCalendar.setTime(projectModel.getDueDate());
            //DateTimeManager.clearTime(targetCalendar);
            //dueDateButton.setText(dateTimeManager.getDateString());
            dueDateButton.setText(DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));
            saveButton.setText(R.string.add_project_title_update_project);
        } else {
            //DateTimeManager.clearTime(targetCalendar);
            projectModel.setDueDate(new Date());
            dueDateButton.setText(DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));
            initEditText(activity);
        }

        initColorChips(activity);
        initClickEvents(activity);

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        dueDateButton.setText(DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnAddProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }

    private void initEditText(Activity activity) {
        projectNameEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                clearEditTextFocus(activity);
            }
            return false;
        });
        projectNameEditText.requestFocus();
        KeyboardManager.showKeyboard(activity, projectNameEditText);
    }

    private void initColorChips(Activity activity) {
        colorTagChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            clearEditTextFocus(activity);
            if (group.getCheckedChipId() == View.NO_ID) {
                projectModel.setColorTag(null);
                return;
            }
            switch (checkedId) {
                case R.id.chip_red:
                    projectModel.setColorTag(COLOR_RED_HEX);
                    break;
                case R.id.chip_pink:
                    projectModel.setColorTag(COLOR_PINK_HEX);
                    break;
                case R.id.chip_purple:
                    projectModel.setColorTag(COLOR_PURPLE_HEX);
                    break;
                case R.id.chip_deep_purple:
                    projectModel.setColorTag(COLOR_DEEP_PURPLE_HEX);
                    break;
                case R.id.chip_indigo:
                    projectModel.setColorTag(COLOR_INDIGO_HEX);
                    break;
                case R.id.chip_blue:
                    projectModel.setColorTag(COLOR_BLUE_HEX);
                    break;
                case R.id.chip_light_blue:
                    projectModel.setColorTag(COLOR_LIGHT_BLUE_HEX);
                    break;
                case R.id.chip_cyan:
                    projectModel.setColorTag(COLOR_CYAN_HEX);
                    break;
                case R.id.chip_teal:
                    projectModel.setColorTag(COLOR_TEAL_HEX);
                    break;
                case R.id.chip_green:
                    projectModel.setColorTag(COLOR_GREEN_HEX);
                    break;
                case R.id.chip_light_green:
                    projectModel.setColorTag(COLOR_LIGHT_GREEN_HEX);
                    break;
                case R.id.chip_lime:
                    projectModel.setColorTag(COLOR_LIME_HEX);
                    break;
                case R.id.chip_yellow:
                    projectModel.setColorTag(COLOR_YELLOW_HEX);
                    break;
                case R.id.chip_amber:
                    projectModel.setColorTag(COLOR_AMBER_HEX);
                    break;
                case R.id.chip_orange:
                    projectModel.setColorTag(COLOR_ORANGE_HEX);
                    break;
                case R.id.chip_deep_orange:
                    projectModel.setColorTag(COLOR_DEEP_ORANGE_HEX);
                    break;
            }
        });
    }

    private void checkColorTag(String colorTagHex) {
        if (colorTagHex == null) {
            return;
        }
        switch (colorTagHex) {
            case COLOR_RED_HEX:
                colorTagChipGroup.check(R.id.chip_red);
                break;
            case COLOR_PINK_HEX:
                colorTagChipGroup.check(R.id.chip_pink);
                break;
            case COLOR_PURPLE_HEX:
                colorTagChipGroup.check(R.id.chip_purple);
                break;
            case COLOR_DEEP_PURPLE_HEX:
                colorTagChipGroup.check(R.id.chip_deep_purple);
                break;
            case COLOR_INDIGO_HEX:
                colorTagChipGroup.check(R.id.chip_indigo);
                break;
            case COLOR_BLUE_HEX:
                colorTagChipGroup.check(R.id.chip_blue);
                break;
            case COLOR_LIGHT_BLUE_HEX:
                colorTagChipGroup.check(R.id.chip_light_blue);
                break;
            case COLOR_CYAN_HEX:
                colorTagChipGroup.check(R.id.chip_cyan);
                break;
            case COLOR_TEAL_HEX:
                colorTagChipGroup.check(R.id.chip_teal);
                break;
            case COLOR_GREEN_HEX:
                colorTagChipGroup.check(R.id.chip_green);
                break;
            case COLOR_LIGHT_GREEN_HEX:
                colorTagChipGroup.check(R.id.chip_light_green);
                break;
            case COLOR_LIME_HEX:
                colorTagChipGroup.check(R.id.chip_lime);
                break;
            case COLOR_YELLOW_HEX:
                colorTagChipGroup.check(R.id.chip_yellow);
                break;
            case COLOR_AMBER_HEX:
                colorTagChipGroup.check(R.id.chip_amber);
                break;
            case COLOR_ORANGE_HEX:
                colorTagChipGroup.check(R.id.chip_orange);
                break;
            case COLOR_DEEP_ORANGE_HEX:
                colorTagChipGroup.check(R.id.chip_deep_orange);
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
                    Context context = getContext();
                    clearEditTextFocus(activity);
                    if (!validateInput(context)) {
                        break;
                    }
                    if (projectModel.isValid()) {
                        saveProject(context);
                    } else if (context != null) {
                        Toast.makeText(
                                context,
                                R.string.add_project_err_name_invalid,
                                Toast.LENGTH_LONG).show();
                    }
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

    private boolean validateInput(Context context) {
        if (isEditing) {
            return true;
        }
        if (Objects.requireNonNull(projectNameEditText.getText()).toString().trim().isEmpty()) {
            Toast.makeText(context, R.string.add_project_err_name_empty, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void showDatePickerDialog(Activity activity) {
        AlertDialog alertDialog = DialogManager.showDialog(activity, R.id.container_main, R.layout.dialog_date_picker);
        DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(projectModel.getDueDate());
        if (datePicker != null) {
            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            datePicker.init(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    getDateChangeListener(activity, alertDialog));
        }
    }

    private DatePicker.OnDateChangedListener getDateChangeListener(Context context, AlertDialog alertDialog) {
        return (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            projectModel.setDueDate(calendar.getTime());
            dueDateButton.setText(DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));
            alertDialog.dismiss();
        };
    }

    private void saveProject(Context context) {
        startSaveStartedAnimation();
        ProjectApi.saveProject(projectModel, task -> {
            if (context == null) {
                startSaveCanceledAnimation();
                return;
            }
            if (task.isSuccessful()) {
                Toast.makeText(
                        context,
                        isEditing ? R.string.add_project_toast_update_success : R.string.add_project_toast_add_success,
                        Toast.LENGTH_SHORT).show();
                fragmentListener.onCloseFragment();
            } else {
                Toast.makeText(
                        context,
                        isEditing ? R.string.add_project_toast_update_failed : R.string.add_project_toast_add_failed,
                        Toast.LENGTH_LONG).show();
                startSaveCanceledAnimation();
            }
        });
    }

    private void startSaveStartedAnimation() {
        saveButton.setEnabled(false);
        TransitionManager.beginDelayedTransition(addProjectLayout);
        constraintSet.clone(addProjectLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.INVISIBLE);
        constraintSet.applyTo(addProjectLayout);
    }

    private void startSaveCanceledAnimation() {
        saveButton.setEnabled(true);
        TransitionManager.beginDelayedTransition(addProjectLayout);
        constraintSet.clone(addProjectLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.VISIBLE);
        constraintSet.applyTo(addProjectLayout);
    }

    private void clearEditTextFocus(Activity activity) {
        if (!isEditing) {
            projectModel.setName(Objects.requireNonNull(projectNameEditText.getText()).toString().trim());
            KeyboardManager.hideKeyboard(activity);
            projectNameEditText.clearFocus();
        }
    }

    public interface OnAddProjectFragmentInteractionListener {
        void onCloseFragment();
    }

}

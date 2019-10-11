package com.glebworx.pomodoro.ui.fragment.add_project;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
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
import com.glebworx.pomodoro.ui.fragment.add_project.interfaces.IAddProjectFragment;
import com.glebworx.pomodoro.ui.fragment.add_project.interfaces.IAddProjectFragmentInteractionListener;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.KeyboardManager;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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


public class AddProjectFragment extends Fragment implements IAddProjectFragment {


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

    static final String ARG_PROJECT_MODEL = "project_model";


    //                                                                                    ATTRIBUTES

    private IAddProjectFragmentInteractionListener fragmentListener;
    private Activity activity;
    private Context context;
    private ConstraintSet constraintSet;
    private Unbinder unbinder;
    private AddProjectFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public AddProjectFragment() { }


    //                                                                                       FACTORY

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


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_project, container, false);
        activity = getActivity();
        context = getContext();
        constraintSet = new ConstraintSet();
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new AddProjectFragmentPresenter(this, getArguments());
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
        fragmentListener = (IAddProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }

    @Override
    public void onInitView(boolean isEditing,
                           String name,
                           String colorTag,
                           String dueDate) {

        if (activity == null || context == null) {
            fragmentListener.onCloseFragment();
        }

        if (isEditing) {
            projectNameEditText.setVisibility(View.GONE);
            projectNameSectionTextView.setVisibility(View.GONE);
            titleTextView.setText(context.getString(R.string.core_edit_something, name));
            checkColorTag(colorTag);
            dueDateButton.setText(dueDate);
            saveButton.setText(R.string.add_project_title_update_project);
        } else {
            dueDateButton.setText(dueDate);
            initEditText();
        }

        initColorChips();
        initClickEvents(activity);

    }

    @Override
    public void onProjectNameChanged() {
        KeyboardManager.hideKeyboard(activity);
        projectNameEditText.clearFocus();
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
    public void onSaveProjectStart() {
        startSaveStartedAnimation();
    }

    @Override
    public void onSaveProjectSuccess(boolean isEditing) {
        Toast.makeText(
                context,
                isEditing ? R.string.add_project_toast_update_success : R.string.add_project_toast_add_success,
                Toast.LENGTH_SHORT).show();
        fragmentListener.onCloseFragment();
    }

    @Override
    public void onSaveProjectFailure(boolean isEditing) {
        Toast.makeText(
                context,
                isEditing ? R.string.add_project_toast_update_failed : R.string.add_project_toast_add_failed,
                Toast.LENGTH_LONG).show();
        startSaveCanceledAnimation();
    }

    @Override
    public void onProjectValidationFailed(boolean isEmpty) {
        Toast.makeText(
                context,
                isEmpty ? R.string.add_project_err_name_empty : R.string.add_project_err_name_invalid,
                Toast.LENGTH_LONG).show();
    }

    private void initEditText() {
        projectNameEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateName();
            }
            return false;
        });
        projectNameEditText.requestFocus();
        KeyboardManager.showKeyboard(activity, projectNameEditText);
    }

    private void initColorChips() {
        colorTagChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateName();
            presenter.selectColorTag(group.getCheckedChipId());
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
                    presenter.editDueDate();
                    break;
                case R.id.button_save:
                    updateName();
                    presenter.saveProject();
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
        AlertDialog alertDialog = DialogManager.showDialog(activity, R.id.container_main, R.layout.dialog_date_picker);
        DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTime(dueDate);
        if (datePicker != null) {
            datePicker.setMinDate(System.currentTimeMillis() - 1000);
            datePicker.init(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    getDateChangeListener(alertDialog));
        }
    }

    private DatePicker.OnDateChangedListener getDateChangeListener(AlertDialog alertDialog) {
        return (view, year, monthOfYear, dayOfMonth) -> {
            presenter.selectDueDate(year, monthOfYear, dayOfMonth);
            alertDialog.dismiss();
        };
    }

    private void startSaveStartedAnimation() {
        saveButton.setEnabled(false);
        TransitionManager.endTransitions(addProjectLayout);
        TransitionManager.beginDelayedTransition(addProjectLayout);
        constraintSet.clone(addProjectLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.INVISIBLE);
        constraintSet.applyTo(addProjectLayout);
    }

    private void startSaveCanceledAnimation() {
        saveButton.setEnabled(true);
        TransitionManager.endTransitions(addProjectLayout);
        TransitionManager.beginDelayedTransition(addProjectLayout);
        constraintSet.clone(addProjectLayout);
        constraintSet.setVisibility(R.id.spin_kit_view, ConstraintSet.INVISIBLE);
        constraintSet.setVisibility(R.id.button_save, ConstraintSet.VISIBLE);
        constraintSet.applyTo(addProjectLayout);
    }

    private void updateName() {
        presenter.editProjectName(Objects.requireNonNull(projectNameEditText.getText()).toString().trim());
    }

}

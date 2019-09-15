package com.glebworx.pomodoro.ui.main.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
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
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.KeyboardManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_ALIZARIN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_AMETHYST_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_CARROT_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_EMERALD_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_PETER_RIVER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_SUNFLOWER_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_TURQUOISE_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_WET_ASPHALT_HEX;


public class AddProjectFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.layout_add_project) ConstraintLayout addProjectLayout;
    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.edit_text_name) AppCompatEditText projectNameEditText;
    @BindView(R.id.chip_group_color) ChipGroup colorTagChipGroup;
    @BindView(R.id.button_due_date) AppCompatButton dueDateButton;
    @BindView(R.id.button_save) AppCompatButton saveButton;
    @BindView(R.id.spin_kit_view) SpinKitView spinKitView;


    //                                                                                    ATTRIBUTES

    private static SimpleDateFormat dateFormat =
            new SimpleDateFormat(Constants.PATTERN_DATE, Locale.getDefault());
    private OnAddProjectFragmentInteractionListener fragmentListener;
    private ConstraintSet constraintSet;
    private ProjectModel projectModel;
    private Calendar calendar;
    private int year;
    private int month;
    private int today;


    //                                                                                     LIFECYCLE

    public AddProjectFragment() { }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_project, container, false);
        ButterKnife.bind(this, rootView);

        constraintSet = new ConstraintSet();
        projectModel = new ProjectModel();
        calendar = Calendar.getInstance(Locale.getDefault());
        updateToday();

        Activity activity = getActivity();
        if (activity == null) {
            return rootView;
        }

        initEditText(activity);
        initColorChips(activity);
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
        projectModel.setColorTag(COLOR_TURQUOISE_HEX);
        colorTagChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            clearEditTextFocus(activity);
            switch (checkedId) {
                case R.id.chip_turquoise:
                    projectModel.setColorTag(COLOR_TURQUOISE_HEX);
                    break;
                case R.id.chip_emerald:
                    projectModel.setColorTag(COLOR_EMERALD_HEX);
                    break;
                case R.id.chip_peter_river:
                    projectModel.setColorTag(COLOR_PETER_RIVER_HEX);
                    break;
                case R.id.chip_amethyst:
                    projectModel.setColorTag(COLOR_AMETHYST_HEX);
                    break;
                case R.id.chip_wet_asphalt:
                    projectModel.setColorTag(COLOR_WET_ASPHALT_HEX);
                    break;
                case R.id.chip_sunflower:
                    projectModel.setColorTag(COLOR_SUNFLOWER_HEX);
                    break;
                case R.id.chip_carrot:
                    projectModel.setColorTag(COLOR_CARROT_HEX);
                    break;
                case R.id.chip_alizarin:
                    projectModel.setColorTag(COLOR_ALIZARIN_HEX);
                    break;
            }
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
                    if (projectModel.isValid()) {
                        saveProject(context);
                    } else if (context != null) {
                        Toast.makeText(context, R.string.add_project_toast_add_failed, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.button_close:
                    clearEditTextFocus(activity);
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        projectModel.setDueDate(calendar.getTime());
        dueDateButton.setText(getString(R.string.core_today));
        dueDateButton.setOnClickListener(onClickListener);
        saveButton.setOnClickListener(onClickListener);
        closeButton.setOnClickListener(onClickListener);
    }

    private boolean validateInput(Context context) {
        if (Objects.requireNonNull(projectNameEditText.getText()).toString().trim().isEmpty()) {
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
            YearMonth yearMonthObject = YearMonth.of(year, monthOfYear);
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

    private void saveProject(Context context) {
        startSaveStartedAnimation();
        ProjectApi.saveModel(projectModel, task -> {
            if (context == null) {
                startSaveCanceledAnimation();
                return;
            }
            if (task.isSuccessful()) {
                Toast.makeText(context, R.string.add_project_toast_add_success, Toast.LENGTH_SHORT).show();
                fragmentListener.onCloseFragment();
            } else {
                Toast.makeText(context, R.string.add_project_toast_add_failed, Toast.LENGTH_LONG).show();
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

    private void updateToday() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.today = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void clearEditTextFocus(Activity activity) {
        projectModel.setName(Objects.requireNonNull(projectNameEditText.getText()).toString().trim());
        KeyboardManager.hideKeyboard(activity);
        projectNameEditText.clearFocus();
    }

    public interface OnAddProjectFragmentInteractionListener {
        void onCloseFragment();
    }

}

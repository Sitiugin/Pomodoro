package com.glebworx.pomodoro.ui.main.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.util.manager.DateTimeManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.glebworx.pomodoro.ui.main.fragment.AddProjectFragment.ARG_PROJECT_MODEL;
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

class AddProjectFragmentPresenter implements IAddProjectFragmentPresenter {

    private ProjectModel projectModel;
    private boolean isEditing;
    private PresenterListener presenterListener;
    private static SparseArray<String> colorTagMap;

    AddProjectFragmentPresenter(@NonNull PresenterListener presenterListener,
                                @Nullable Bundle arguments) {

        this.presenterListener = presenterListener;
        initColorTagMap();
        init(arguments);
    }

    private void initColorTagMap() {
        colorTagMap = new SparseArray<>();
        colorTagMap.put(View.NO_ID, null);
        colorTagMap.put(R.id.chip_red, COLOR_RED_HEX);
        colorTagMap.put(R.id.chip_pink, COLOR_PINK_HEX);
        colorTagMap.put(R.id.chip_purple, COLOR_PURPLE_HEX);
        colorTagMap.put(R.id.chip_deep_purple, COLOR_DEEP_PURPLE_HEX);
        colorTagMap.put(R.id.chip_indigo, COLOR_INDIGO_HEX);
        colorTagMap.put(R.id.chip_blue, COLOR_BLUE_HEX);
        colorTagMap.put(R.id.chip_light_blue, COLOR_LIGHT_BLUE_HEX);
        colorTagMap.put(R.id.chip_cyan, COLOR_CYAN_HEX);
        colorTagMap.put(R.id.chip_teal, COLOR_TEAL_HEX);
        colorTagMap.put(R.id.chip_green, COLOR_GREEN_HEX);
        colorTagMap.put(R.id.chip_light_green, COLOR_LIGHT_GREEN_HEX);
        colorTagMap.put(R.id.chip_lime, COLOR_LIME_HEX);
        colorTagMap.put(R.id.chip_yellow, COLOR_YELLOW_HEX);
        colorTagMap.put(R.id.chip_amber, COLOR_AMBER_HEX);
        colorTagMap.put(R.id.chip_orange, COLOR_ORANGE_HEX);
        colorTagMap.put(R.id.chip_deep_orange, COLOR_DEEP_ORANGE_HEX);
    }

    @Override
    public void init(Bundle arguments) {
        if (arguments != null) {
            projectModel = arguments.getParcelable(ARG_PROJECT_MODEL);
        }
        if (projectModel == null) {
            isEditing = false;
            projectModel = new ProjectModel();
            projectModel.setDueDate(new Date());
        } else {
            isEditing = true;
        }
        presenterListener.onInitView(
                isEditing,
                projectModel.getName(),
                projectModel.getColorTag(),
                DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));
    }

    @Override
    public void updateProjectName(String name) {
        if (!isEditing) {
            projectModel.setName(name);
            presenterListener.onProjectNameUpdated();
        }
    }

    @Override
    public void updateColorTag(int checkedId) {
        projectModel.setColorTag(colorTagMap.get(checkedId));
    }

    @Override
    public void updateDueDate() {
        presenterListener.onUpdateDueDate(projectModel.getDueDate());
    }

    @Override
    public void changeDueDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        projectModel.setDueDate(calendar.getTime());
        presenterListener.onChangeDueDate(DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));
    }

    @Override
    public void saveProject() {
        if (projectModel.isValid()) {
            presenterListener.onSaveProjectStart();
            ProjectApi.saveProject(projectModel, task -> {
                if (task.isSuccessful()) {
                    presenterListener.onSaveProjectSuccess(isEditing);
                } else {
                    presenterListener.onSaveProjectFailure(isEditing);
                }
            });
        } else {
            presenterListener.onProjectValidationFailed(
                    projectModel.getName() == null
                            || projectModel.getName().isEmpty());
        }
    }

    public interface PresenterListener {
        void onInitView(boolean isEditing,
                        String name,
                        String colorTag,
                        String dueDate);
        void onProjectNameUpdated();
        void onUpdateDueDate(Date dueDate);
        void onChangeDueDate(String dateString);
        void onSaveProjectStart();
        void onSaveProjectSuccess(boolean isEditing);
        void onSaveProjectFailure(boolean isEditing);
        void onProjectValidationFailed(boolean isEmpty);
    }

}

package com.glebworx.pomodoro.ui.fragment.add_project;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.add_project.interfaces.IAddProjectFragment;
import com.glebworx.pomodoro.ui.fragment.add_project.interfaces.IAddProjectFragmentPresenter;
import com.glebworx.pomodoro.util.manager.DateTimeManager;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.glebworx.pomodoro.ui.fragment.add_project.AddProjectFragment.ARG_PROJECT_MODEL;
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


    //                                                                                     CONSTANTS

    private static final Map<Integer, String> CHIP_TO_COLOR_TAG_MAP;
    private static final Map<String, Integer> COLOR_TAG_TO_CHIP_MAP;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(View.NO_ID, null);
        map.put(R.id.chip_red, COLOR_RED_HEX);
        map.put(R.id.chip_pink, COLOR_PINK_HEX);
        map.put(R.id.chip_purple, COLOR_PURPLE_HEX);
        map.put(R.id.chip_deep_purple, COLOR_DEEP_PURPLE_HEX);
        map.put(R.id.chip_indigo, COLOR_INDIGO_HEX);
        map.put(R.id.chip_blue, COLOR_BLUE_HEX);
        map.put(R.id.chip_light_blue, COLOR_LIGHT_BLUE_HEX);
        map.put(R.id.chip_cyan, COLOR_CYAN_HEX);
        map.put(R.id.chip_teal, COLOR_TEAL_HEX);
        map.put(R.id.chip_green, COLOR_GREEN_HEX);
        map.put(R.id.chip_light_green, COLOR_LIGHT_GREEN_HEX);
        map.put(R.id.chip_lime, COLOR_LIME_HEX);
        map.put(R.id.chip_yellow, COLOR_YELLOW_HEX);
        map.put(R.id.chip_amber, COLOR_AMBER_HEX);
        map.put(R.id.chip_orange, COLOR_ORANGE_HEX);
        map.put(R.id.chip_deep_orange, COLOR_DEEP_ORANGE_HEX);
        CHIP_TO_COLOR_TAG_MAP = Collections.unmodifiableMap(map);
    }

    static {
        HashMap<String, Integer> map = new HashMap<>();
        map.put(COLOR_RED_HEX, R.id.chip_red);
        map.put(COLOR_PINK_HEX, R.id.chip_pink);
        map.put(COLOR_PURPLE_HEX, R.id.chip_purple);
        map.put(COLOR_DEEP_PURPLE_HEX, R.id.chip_deep_purple);
        map.put(COLOR_INDIGO_HEX, R.id.chip_indigo);
        map.put(COLOR_BLUE_HEX, R.id.chip_blue);
        map.put(COLOR_LIGHT_BLUE_HEX, R.id.chip_light_blue);
        map.put(COLOR_CYAN_HEX, R.id.chip_cyan);
        map.put(COLOR_TEAL_HEX, R.id.chip_teal);
        map.put(COLOR_GREEN_HEX, R.id.chip_green);
        map.put(COLOR_LIGHT_GREEN_HEX, R.id.chip_light_green);
        map.put(COLOR_LIME_HEX, R.id.chip_lime);
        map.put(COLOR_YELLOW_HEX, R.id.chip_yellow);
        map.put(COLOR_AMBER_HEX, R.id.chip_amber);
        map.put(COLOR_ORANGE_HEX, R.id.chip_orange);
        map.put(COLOR_DEEP_ORANGE_HEX, R.id.chip_deep_orange);
        COLOR_TAG_TO_CHIP_MAP = Collections.unmodifiableMap(map);
    }

    //                                                                                    ATTRIBUTES

    private IAddProjectFragment presenterListener;
    private ProjectModel projectModel;
    private boolean isEditing;


    //                                                                                  CONSTRUCTORS

    AddProjectFragmentPresenter(@NonNull IAddProjectFragment presenterListener,
                                @Nullable Bundle arguments) {
        this.presenterListener = presenterListener;
        init(arguments);
    }


    //                                                                                IMPLEMENTATION

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

        int checkedChipId;
        if (projectModel.getColorTag() == null) {
            checkedChipId = 0;
        } else {
            checkedChipId = COLOR_TAG_TO_CHIP_MAP.get(projectModel.getColorTag());
        }
        presenterListener.onInitView(
                isEditing,
                projectModel.getName(),
                checkedChipId,
                DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));

    }

    @Override
    public void editProjectName(String name) {
        if (!isEditing) {
            projectModel.setName(name);
            presenterListener.onProjectNameChanged();
        }
    }

    @Override
    public void selectColorTag(int checkedId) {
        projectModel.setColorTag(CHIP_TO_COLOR_TAG_MAP.get(checkedId));
    }

    @Override
    public void editDueDate() {
        presenterListener.onEditDueDate(projectModel.getDueDate());
    }

    @Override
    public void selectDueDate(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
        projectModel.setDueDate(calendar.getTime());
        presenterListener.onSelectDueDate(DateTimeManager.getDateString(projectModel.getDueDate(), new Date()));
    }

    @Override
    public void saveProject() {

        if (projectModel.isValid()) {

            if (isEditing) {
                updateProject();
                presenterListener.onSaveProject(true);
            } else {
                addProject();
                presenterListener.onSaveProject(false);
            }

        } else {

            presenterListener.onProjectValidationFailed(
                    projectModel.getName() == null
                            || projectModel.getName().isEmpty());

        }

    }


    //                                                                                       HELPERS

    private void addProject() {
        ProjectApi.addProject(projectModel, null);
    }

    private void updateProject() {
        ProjectApi.updateProject(projectModel, null);
    }

}

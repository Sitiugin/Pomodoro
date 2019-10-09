package com.glebworx.pomodoro.util;

import com.glebworx.pomodoro.ui.fragment.projects.item.ProjectItem;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_RED_HEX;


public class DummyDataProvider { // TODO temp class for testing

    private DummyDataProvider() { }

    public static List<TaskItem> getTasks() {
        List<TaskItem> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TaskModel model = new TaskModel("Some task", 5, new Date(), "Section 1", TaskModel.RECURRENCE_EVERY_DAY);
            result.add(new TaskItem(model));
        }
        return result;
    }

    public static List<ProjectItem> getProjects() {
        List<ProjectItem> result  = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ProjectModel model = new ProjectModel("Some Project", new Date(), COLOR_RED_HEX, null);
            result.add(new ProjectItem(model));
        }
        return result;
    }
}

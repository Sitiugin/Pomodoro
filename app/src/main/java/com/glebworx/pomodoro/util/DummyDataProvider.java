package com.glebworx.pomodoro.util;

import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_EMERALD_HEX;

public class DummyDataProvider { // TODO temp class for testing

    private DummyDataProvider() { }

    public static List<TaskItem> getTasks() {
        List<TaskItem> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TaskModel model = new TaskModel("Some task", "Some project", 5, new Date(), 0);
            result.add(new TaskItem(model));
        }
        return result;
    }

    public static List<ProjectItem> getProjects() {
        List<ProjectItem> result  = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<TaskModel> taskModels = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                TaskModel model = new TaskModel("Some task", "Some project", 5, null, 0);
                model.setPomodorosCompleted(3);
                taskModels.add(model);
            }
            ProjectModel model = new ProjectModel("Some Project", new Date(), COLOR_EMERALD_HEX, taskModels);
            result.add(new ProjectItem(model));
        }
        return result;
    }
}

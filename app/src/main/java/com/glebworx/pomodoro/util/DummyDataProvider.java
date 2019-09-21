package com.glebworx.pomodoro.util;

import com.glebworx.pomodoro.item.ProjectItem;
import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_EMERALD_HEX;

public class DummyDataProvider { // TODO temp class for testing

    private DummyDataProvider() { }

    public static List<TaskItem> getTasks() {
        List<TaskItem> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TaskModel model = new TaskModel("Some task", 5, new Date(), TaskModel.RECURRENCE_EVERY_DAY);
            result.add(new TaskItem(model));
        }
        return result;
    }

    public static List<ProjectItem> getProjects() {
        List<ProjectItem> result  = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, TaskModel> taskModels = new HashMap<>();
            for (int j = 0; j < 10; j++) {
                TaskModel model = new TaskModel("Some task", 5, null, null);
                model.setPomodorosCompleted(3);
                taskModels.put(model.getName(), model);
            }
            ProjectModel model = new ProjectModel("Some Project", new Date(), COLOR_EMERALD_HEX);
            result.add(new ProjectItem(model));
        }
        return result;
    }
}

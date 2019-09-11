package com.glebworx.pomodoro.util;

import com.glebworx.pomodoro.item.TaskItem;
import com.glebworx.pomodoro.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class DummyDataProvider { // TODO temp class for testing

    private DummyDataProvider() { }

    public static List<TaskItem> getTasks() {
        List<TaskItem> result = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TaskModel model = new TaskModel("Some task", "Some project", 5, null, 0);
            result.add(new TaskItem(model));
        }
        return result;
    }
}

package com.glebworx.pomodoro.ui.fragment.view_tasks.item;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.model.TaskModel;
import com.glebworx.pomodoro.ui.fragment.view_project.item.TaskItem;

public class ViewTaskItem extends TaskItem {

    public ViewTaskItem(@NonNull TaskModel model) {
        super(model);
    }

    @Override
    public boolean isSwipeable() {
        return false;
    }

}

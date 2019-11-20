package com.glebworx.pomodoro.ui.fragment.add_project.interfaces;

import java.util.Date;

public interface IAddProjectFragment {

    void onInitView(boolean isEditing,
                    String name,
                    int checkedChipId,
                    String dueDate);

    void onProjectNameChanged();

    void onEditDueDate(Date dueDate);

    void onSelectDueDate(String dateString);

    void onSaveProject(boolean isEditing);

    void onProjectValidationFailed(boolean isEmpty);

}

package com.glebworx.pomodoro.ui.fragment.add_project.interfaces;

import java.util.Date;

public interface IAddProjectFragment {

    void onInitView(boolean isEditing,
                    String name,
                    String colorTag,
                    String dueDate);

    void onProjectNameChanged();

    void onEditDueDate(Date dueDate);

    void onSelectDueDate(String dateString);

    void onSaveProjectStart();

    void onSaveProjectSuccess(boolean isEditing);

    void onSaveProjectFailure(boolean isEditing);

    void onProjectValidationFailed(boolean isEmpty);

}

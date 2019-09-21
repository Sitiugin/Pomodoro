package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class TaskApi extends BaseApi {

    //                                                                                     CONSTANTS

    private static final String COLLECTION_PROJECTS = "projects";
    private static final String COLLECTION_TASKS = "tasks";


    //                                                                       CONSTRUCTOR SUPPRESSION

    private TaskApi() { }


    //                                                                                    PUBLIC API


    public static void addTask(@NonNull String projectName,
                               @NonNull TaskModel taskModel,
                               @Nullable OnCompleteListener<Void> onCompleteListener) {

        Task<Void> task = getCollection(COLLECTION_PROJECTS)
                .document(projectName)
                .collection(COLLECTION_TASKS)
                .document(taskModel.getName())
                .set(taskModel);

        // TODO batch update project data

        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }

    }

    public static void removeTask(@NonNull String projectName,
                                  @NonNull TaskModel taskModel,
                                  @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = getCollection(COLLECTION_PROJECTS)
                .document(projectName)
                .collection(COLLECTION_TASKS)
                .document(taskModel.getName())
                .delete();

        // TODO batch update project data
        // TODO delete collection if necessary
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

}

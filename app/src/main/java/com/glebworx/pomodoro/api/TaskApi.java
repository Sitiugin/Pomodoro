package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class TaskApi extends BaseApi {

    //                                                                                     CONSTANTS

    private static final String COLLECTION_PROJECTS = "projects";
    private static final String COLLECTION_TASKS = "tasks";


    //                                                                       CONSTRUCTOR SUPPRESSION

    private TaskApi() { }


    //                                                                                    PUBLIC API


    public static void addTask(@NonNull ProjectModel projectModel,
                               @NonNull TaskModel taskModel,
                               @Nullable OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.set(
                projectDocument.collection(COLLECTION_TASKS).document(taskModel.getName()),
                taskModel);

        batch.update(projectDocument,
                "tasks", projectModel.getTasks(),
                "pomodorosAllocated", projectModel.getPomodorosAllocated(),
                "pomodorosCompleted", projectModel.getPomodorosCompleted());

        if (onCompleteListener != null) {
            batch.commit().addOnCompleteListener(onCompleteListener);
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

    public static void addModelEventListener(@NonNull String projectName,
                                             @NonNull EventListener<QuerySnapshot> eventListener) {
        getCollection(COLLECTION_PROJECTS)
                .document(projectName)
                .collection(COLLECTION_TASKS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(eventListener);
    }

}

package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.List;

public class TaskApi extends BaseApi {

    //                                                                                     CONSTANTS


    //                                                                       CONSTRUCTOR SUPPRESSION

    private TaskApi() { }


    //                                                                                    PUBLIC API


    public static void addTask(@NonNull ProjectModel projectModel,
                               @NonNull TaskModel taskModel,
                               //@Nullable TaskModel oldTaskModel,
                               @Nullable OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.set(
                projectDocument.collection(COLLECTION_TASKS).document(taskModel.getName()),
                taskModel);

        batch.update(projectDocument,
                FIELD_TASKS, projectModel.getTasks(),
                FIELD_POMODOROS_ALLOCATED, projectModel.getPomodorosAllocated(),
                FIELD_POMODOROS_COMPLETED, projectModel.getPomodorosCompleted());

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void deleteTask(@NonNull ProjectModel projectModel,
                                  @NonNull TaskModel taskModel,
                                  @Nullable OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.delete(projectDocument.collection(COLLECTION_TASKS).document(taskModel.getName()));

        batch.update(projectDocument,
                FIELD_TASKS, projectModel.getTasks(),
                FIELD_POMODOROS_ALLOCATED, projectModel.getPomodorosAllocated(),
                FIELD_POMODOROS_COMPLETED, projectModel.getPomodorosCompleted());

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void addAllTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        getCollectionGroup(COLLECTION_TASKS)
                .orderBy(FIELD_DUE_DATE, Query.Direction.DESCENDING)
                .addSnapshotListener(eventListener);
    }

    public static void addTaskEventListener(@NonNull String projectName,
                                            @NonNull EventListener<QuerySnapshot> eventListener) {
        getCollection(COLLECTION_PROJECTS)
                .document(projectName)
                .collection(COLLECTION_TASKS)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(eventListener);
    }

}

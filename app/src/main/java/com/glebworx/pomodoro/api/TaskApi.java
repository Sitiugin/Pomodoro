package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import io.reactivex.Observable;

public class TaskApi extends BaseApi {

    //                                                                                     CONSTANTS


    //                                                                       CONSTRUCTOR SUPPRESSION

    private TaskApi() { }


    //                                                                                    PUBLIC API


    public static void addTask(@NonNull ProjectModel projectModel,
                               @NonNull TaskModel taskModel,
                               @Nullable OnCompleteListener<Void> onCompleteListener) {
        modifyTask(projectModel, taskModel, HistoryModel.EVENT_TASK_CREATED, onCompleteListener);
    }

    public static void completePomodoro(@NonNull ProjectModel projectModel,
                                        @NonNull TaskModel taskModel,
                                        @Nullable OnCompleteListener<Void> onCompleteListener) {
        modifyTask(projectModel, taskModel, HistoryModel.EVENT_POMODORO_COMPLETED, onCompleteListener);
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

        batch.set(
                projectDocument.collection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), taskModel.getName(), HistoryModel.EVENT_TASK_DELETED));

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void addAllTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        getCollectionGroup(COLLECTION_TASKS)
                .orderBy(FIELD_DUE_DATE, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    public static Observable<DocumentChange> getTaskEventObservable(@NonNull String projectName) {
        return Observable.create(emitter -> {
            ListenerRegistration listenerRegistration = TaskApi.addTaskEventListener(projectName, (querySnapshot, e) -> {
                if (e != null) {
                    emitter.onError(e);
                    return;
                }
                if (querySnapshot == null || querySnapshot.isEmpty()) {
                    return;
                }
                List<DocumentChange> documentChanges = querySnapshot.getDocumentChanges();
                for (DocumentChange change : documentChanges) {
                    emitter.onNext(change);
                }
            });
            emitter.setCancellable(listenerRegistration::remove);
        });
    }

    public static ListenerRegistration addTaskEventListener(@NonNull String projectName,
                                                            @NonNull EventListener<QuerySnapshot> eventListener) {
        return getCollection(COLLECTION_PROJECTS)
                .document(projectName)
                .collection(COLLECTION_TASKS)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    private static void modifyTask(@NonNull ProjectModel projectModel,
                                   @NonNull TaskModel taskModel,
                                   @NonNull String eventType,
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

        batch.set(
                projectDocument.collection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), taskModel.getName(), eventType));

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

}

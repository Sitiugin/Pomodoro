package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.ProjectModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_CREATED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_DELETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_UPDATED;

public class ProjectApi extends BaseApi {

    //                                                                                     CONSTANTS


    //                                                                       CONSTRUCTOR SUPPRESSION

    private ProjectApi() { }


    //                                                                                    PUBLIC API

    public static void addProject(@NonNull ProjectModel model,
                                  @Nullable OnCompleteListener<Void> onCompleteListener) {
        modifyProject(model, EVENT_PROJECT_CREATED, onCompleteListener);
    }

    public static void updateProject(@NonNull ProjectModel model,
                                     @Nullable OnCompleteListener<Void> onCompleteListener) {
        modifyProject(model, EVENT_PROJECT_UPDATED, onCompleteListener);
    }

    /*public static void updateTasks(@NonNull ProjectModel projectModel,
                                   @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = getCollection(COLLECTION)
                .document(projectModel.getName())
                .update("tasks", projectModel.getTasksAsMap());
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }*/

    public static void getModel(@NonNull String projectName,
                                @NonNull OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        getModel(projectName, getCollection(COLLECTION_PROJECTS), onCompleteListener);
    }

    public static void getModelByName(@NonNull String name,
                                      @NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getCollection(COLLECTION_PROJECTS).whereEqualTo(FIELD_NAME, name).get().addOnCompleteListener(onCompleteListener);
    }

    public static void deleteProject(@NonNull ProjectModel projectModel,
                                     @Nullable OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());
        CollectionReference tasksCollection = projectDocument.collection(COLLECTION_TASKS);

        List<String> tasks = projectModel.getTasks();
        for (String taskName: tasks) {
            batch.delete(tasksCollection.document(taskName));
        }
        batch.delete(projectDocument);

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), null, EVENT_PROJECT_DELETED)
        );

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    private static void modifyProject(@NonNull ProjectModel projectModel,
                                      @NonNull String eventType,
                                      @Nullable OnCompleteListener<Void> onCompleteListener) {

        projectModel.updateTimestamp();

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.set(projectDocument, projectModel);

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), null, eventType)
        );

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static ListenerRegistration addModelEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        return addModelEventListener(eventListener, getCollection(COLLECTION_PROJECTS));
    }

    public static ListenerRegistration addDocumentModelEventListener(@NonNull String documentName, @NonNull EventListener<DocumentSnapshot> eventListener) {
        return addDocumentModelEventListener(documentName, eventListener, getCollection(COLLECTION_PROJECTS));
    }

}

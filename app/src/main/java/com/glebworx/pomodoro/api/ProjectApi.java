package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class ProjectApi extends BaseApi {

    //                                                                                     CONSTANTS


    //                                                                       CONSTRUCTOR SUPPRESSION

    private ProjectApi() { }


    //                                                                                    PUBLIC API

    public static void saveProject(@NonNull ProjectModel model,
                                   @Nullable OnCompleteListener<Void> onCompleteListener) {
        saveModel(model, getCollection(COLLECTION_PROJECTS), onCompleteListener);
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

    public static void getModel(@NonNull ProjectModel model,
                                @NonNull OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        getModel(model, getCollection(COLLECTION_PROJECTS), onCompleteListener);
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

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static ListenerRegistration addModelEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        return addModelEventListener(eventListener, getCollection(COLLECTION_PROJECTS));
    }

    public static void addDocumentModelEventListener(@NonNull EventListener<DocumentSnapshot> eventListener, @NonNull String documentName) {
        addDocumentModelEventListener(eventListener,  getCollection(COLLECTION_PROJECTS), documentName);
    }

    public interface OnValidationCompleteListener {
        void onConnectionFailed();
        void onDuplicateFound();
    }

}

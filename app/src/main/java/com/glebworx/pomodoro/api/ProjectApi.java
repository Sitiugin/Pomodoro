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
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.List;

import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_COMPLETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_CREATED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_DELETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_RESTORED;
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

    public static void getProject(@NonNull String projectName,
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

    public static void completeProject(@NonNull ProjectModel projectModel,
                                       @Nullable OnCompleteListener<Void> onCompleteListener) {

        projectModel.updateTimestamp();

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.update(projectDocument, FIELD_COMPLETED, true);
        batch.update(projectDocument, FIELD_COMPLETED_ON, new Date());

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), null, EVENT_PROJECT_COMPLETED)
        );

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void restoreProject(@NonNull ProjectModel projectModel,
                                      @Nullable OnCompleteListener<Void> onCompleteListener) {

        projectModel.updateTimestamp();

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.update(projectDocument, FIELD_COMPLETED, false);

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), null, EVENT_PROJECT_RESTORED)
        );

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void deleteProjects(List<ProjectModel> projectModels, @Nullable OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = getWriteBatch();

        CollectionReference projectsCollection = getCollection(COLLECTION_PROJECTS);

        for (ProjectModel projectModel : projectModels) {

            DocumentReference projectDocument = projectsCollection.document(projectModel.getName());

            batch.delete(projectDocument);

            batch.set(
                    getCollection(COLLECTION_HISTORY).document(),
                    new HistoryModel(projectModel.getName(), projectModel.getColorTag(), null, EVENT_PROJECT_DELETED)
            );

        }

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

    public static ListenerRegistration addProjectsEventListener(@NonNull EventListener<QuerySnapshot> eventListener, boolean completed) {
        return getCollection(COLLECTION_PROJECTS)
                .whereEqualTo(FIELD_COMPLETED, completed)
                //.orderBy(FIELD_COMPLETED, Query.Direction.DESCENDING)
                //.orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    public static ListenerRegistration addProjectEventListener(@NonNull String projectName, @NonNull EventListener<DocumentSnapshot> eventListener) {
        return addDocumentModelEventListener(projectName, eventListener, getCollection(COLLECTION_PROJECTS));
    }

}

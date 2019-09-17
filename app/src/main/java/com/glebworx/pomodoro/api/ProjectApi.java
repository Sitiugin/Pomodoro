package com.glebworx.pomodoro.api;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class ProjectApi extends BaseApi {

    //                                                                                     CONSTANTS

    private static final String COLLECTION = "projects";


    //                                                                       CONSTRUCTOR SUPPRESSION

    private ProjectApi() { }


    //                                                                                    PUBLIC API

    public static void saveModel(@NonNull ProjectModel model,
                                 @Nullable OnCompleteListener<Void> onCompleteListener) {
        saveModel(model, getCollection(COLLECTION), onCompleteListener);
    }

    public static void updateTasks(@NonNull ProjectModel projectModel,
                                   @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = getCollection(COLLECTION)
                .document(projectModel.getName())
                .update("tasks", projectModel.getTasksAsMap());
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

    public static void getModel(@NonNull ProjectModel model,
                                @NonNull OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        getModel(model, getCollection(COLLECTION), onCompleteListener);
    }

    public static void getModelByName(@NonNull String name,
                                @NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getCollection(COLLECTION).whereEqualTo("name", name).get().addOnCompleteListener(onCompleteListener);
    }

    public static void deleteModel(@NonNull ProjectModel model,
                                   @Nullable OnCompleteListener<Void> onCompleteListener) {
        deleteModel(model, getCollection(COLLECTION), onCompleteListener);
    }

    public static void addModelEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        addModelEventListener(eventListener, getCollection(COLLECTION));
    }

    public interface OnValidationCompleteListener {
        void onConnectionFailed();
        void onDuplicateFound();
    }

}

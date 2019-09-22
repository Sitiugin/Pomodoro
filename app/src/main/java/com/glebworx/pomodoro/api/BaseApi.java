package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.AbstractModel;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public abstract class BaseApi {

    //                                                                             PRIVATE CONSTANTS

    private static final String COLLECTION_GLOBAL = "global";
    private static final String COLLECTION_USERS = "users";
    static final String COLLECTION_PROJECTS = "projects";
    static final String COLLECTION_TASKS = "tasks";

    static final String FIELD_NAME = "name";
    static final String FIELD_TIMESTAMP = "timestamp";
    static final String FIELD_TASKS = "tasks";
    static final String FIELD_POMODOROS_ALLOCATED = "pomodorosAllocated";
    static final String FIELD_POMODOROS_COMPLETED = "pomodorosCompleted";


    //                                                                                   CONSTRUCTOR

    protected BaseApi() { }


    //                                                                                           API

    protected static void saveModel(@NonNull AbstractModel model,
                                    @NonNull CollectionReference collectionReference,
                                    @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = collectionReference.document(model.getName()).set(model);
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

    protected static void getModel(@NonNull AbstractModel model,
                                   @NonNull CollectionReference collectionReference,
                                   @NonNull OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collectionReference.document(model.getName()).get().addOnCompleteListener(onCompleteListener);
    }

    protected static void deleteModel(@NonNull AbstractModel model,
                                      @NonNull CollectionReference collectionReference,
                                      @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = collectionReference.document(model.getName()).delete();
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

    protected static void addModelEventListener(@NonNull EventListener<QuerySnapshot> eventListener,
                                                @NonNull CollectionReference collectionReference) {
        collectionReference.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(eventListener);
    }

    protected static void addDocumentModelEventListener(@NonNull EventListener<DocumentSnapshot> eventListener,
                                                        @NonNull CollectionReference collectionReference,
                                                        @NonNull String documentName) {
        collectionReference.document(documentName).addSnapshotListener(eventListener);
    }


    //                                                                                       HELPERS

    static DocumentReference getGlobalDocument() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_GLOBAL).document(AuthManager.getInstance().getUid());
    }

    static DocumentReference getUserDocument() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(AuthManager.getInstance().getUid());
    }

    static WriteBatch getWriteBatch() {
        return FirebaseFirestore.getInstance().batch();
    }


    //                                                                                       HELPERS

    protected static CollectionReference getCollection(String collection) {
        return getUserDocument().collection(collection);
    }

}

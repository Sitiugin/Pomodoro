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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public abstract class BaseApi {


    //                                                                                     CONSTANTS

    private static final String COLLECTION_GLOBAL = "global";
    private static final String COLLECTION_USERS = "users";

    static final String COLLECTION_PROJECTS = "projects";
    static final String COLLECTION_TASKS = "tasks";
    static final String COLLECTION_HISTORY = "history";

    static final String FIELD_NAME = "name";
    static final String FIELD_TIMESTAMP = "timestamp";
    static final String FIELD_TASKS = "tasks";
    static final String FIELD_COMPLETED = "completed";
    static final String FIELD_COMPLETED_ON = "completedOn";
    static final String FIELD_POMODOROS_ALLOCATED = "pomodorosAllocated";
    static final String FIELD_POMODOROS_COMPLETED = "pomodorosCompleted";
    static final String FIELD_DUE_DATE = "dueDate";


    //                                                                                   CONSTRUCTOR

    BaseApi() {
    }


    //                                                                                           API

    protected static void saveModel(@NonNull AbstractModel model,
                                    @NonNull CollectionReference collectionReference,
                                    @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = collectionReference.document(model.getName()).set(model);
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

    protected static void getModel(@NonNull String name,
                                   @NonNull CollectionReference collectionReference,
                                   @NonNull OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        collectionReference.document(name).get().addOnCompleteListener(onCompleteListener);
    }

    protected static void deleteModel(@NonNull AbstractModel model,
                                      @NonNull CollectionReference collectionReference,
                                      @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = collectionReference.document(model.getName()).delete();
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

    protected static ListenerRegistration addModelEventListener(@NonNull EventListener<QuerySnapshot> eventListener,
                                                                @NonNull CollectionReference collectionReference) {
        return collectionReference.orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING).addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    protected static ListenerRegistration addDocumentModelEventListener(@NonNull String documentName,
                                                                        @NonNull EventListener<DocumentSnapshot> eventListener,
                                                                        @NonNull CollectionReference collectionReference) {
        return collectionReference.document(documentName).addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }


    //                                                                                       HELPERS

    static DocumentReference getGlobalDocument() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_GLOBAL).document(getUserId());
    }

    static DocumentReference getUserDocument() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(getUserId());
    }

    static WriteBatch getWriteBatch() {
        return FirebaseFirestore.getInstance().batch();
    }


    //                                                                                       HELPERS

    protected static String getUserId() {
        return AuthManager.getInstance().getUid();
    }

    protected static CollectionReference getCollection(@NonNull String collection) {
        return getUserDocument().collection(collection);
    }

    protected static Query getCollectionGroup(@NonNull String collection) {
        return FirebaseFirestore.getInstance().collectionGroup(collection);
    }

}

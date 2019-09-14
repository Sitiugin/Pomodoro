package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.AbstractModel;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public abstract class BaseApi {

    //                                                                             PRIVATE CONSTANTS

    private static final String COLLECTION_GLOBAL = "global";
    private static final String COLLECTION_USERS = "users";


    //                                                                                   CONSTRUCTOR

    protected BaseApi() { }


    //                                                                                           API

    protected static void saveModel(@NonNull AbstractModel model,
                                    @NonNull CollectionReference collectionReference,
                                    @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = collectionReference.document(model.getId()).set(model);
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

    protected static void deleteModel(@NonNull AbstractModel model,
                                      @NonNull CollectionReference collectionReference,
                                      @Nullable OnCompleteListener<Void> onCompleteListener) {
        Task<Void> task = collectionReference.document(model.getId()).delete();
        if (onCompleteListener != null) {
            task.addOnCompleteListener(onCompleteListener);
        }
    }

    protected static void addModelEventListener(@NonNull EventListener<QuerySnapshot> eventListener,
                                                @NonNull CollectionReference collectionReference) {
        collectionReference.orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(eventListener);
    }


    //                                                                                       HELPERS

    static DocumentReference getGlobalDocument() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_GLOBAL).document(AuthManager.getInstance().getUid());
    }

    static DocumentReference getUserDocument() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document(AuthManager.getInstance().getUid());
    }


    //                                                                                       HELPERS

    protected static CollectionReference getCollection(String collection) {
        return getUserDocument().collection(collection);
    }

}

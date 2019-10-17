package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static com.glebworx.pomodoro.api.BaseApi.COLLECTION_HISTORY;
import static com.glebworx.pomodoro.api.BaseApi.FIELD_TIMESTAMP;
import static com.glebworx.pomodoro.api.BaseApi.getCollectionGroup;

public class HistoryApi {


    //                                                                       CONSTRUCTOR SUPPRESSION

    private HistoryApi() {
    }


    //                                                                                    PUBLIC API

    public static ListenerRegistration addAllHistoryEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        return getCollectionGroup(COLLECTION_HISTORY)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }


}

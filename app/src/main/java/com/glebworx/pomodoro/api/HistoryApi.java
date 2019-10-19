package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class HistoryApi extends BaseApi {


    //                                                                                     CONSTANTS

    private static final int PAGE_SIZE = 10;


    //                                                                       CONSTRUCTOR SUPPRESSION

    private HistoryApi() {
    }


    //                                                                                    PUBLIC API

    public static ListenerRegistration addAllHistoryEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        return getCollection(COLLECTION_HISTORY)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    public static void getHistory(DocumentSnapshot startAfterSnapshot, @NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        Query query = getCollection(COLLECTION_HISTORY)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .limit(PAGE_SIZE);
        if (startAfterSnapshot != null) {
            query.startAfter(startAfterSnapshot);
        }
        query.get().addOnCompleteListener(onCompleteListener);
    }


}

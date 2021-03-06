package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.glebworx.pomodoro.model.HistoryModel.EVENT_POMODORO_COMPLETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_PROJECT_COMPLETED;
import static com.glebworx.pomodoro.model.HistoryModel.EVENT_TASK_COMPLETED;

public class HistoryApi extends BaseApi {


    //                                                                                     CONSTANTS

    private static final int PAGE_SIZE = 100;

    private static final String FIELD_EVENT_TYPE = "eventType";


    //                                                                       CONSTRUCTOR SUPPRESSION

    private HistoryApi() {
    }


    //                                                                                    PUBLIC API
    public static ListenerRegistration addTodayEventListener(
            @NonNull EventListener<QuerySnapshot> eventListener) {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return getCollection(COLLECTION_HISTORY)
                //.orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(FIELD_EVENT_TYPE, EVENT_POMODORO_COMPLETED)
                .whereGreaterThanOrEqualTo(FIELD_TIMESTAMP, today)
                .addSnapshotListener(eventListener);
    }

    public static void getHistory(@NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getCollection(COLLECTION_HISTORY)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getHistoryAfter(@NonNull DocumentSnapshot startAfterSnapshot,
                                       @NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getCollection(COLLECTION_HISTORY)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .startAfter(startAfterSnapshot)
                .limit(PAGE_SIZE).get().addOnCompleteListener(onCompleteListener);
    }

    public static void getPomodoroCompletionHistory(@NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getCollection(COLLECTION_HISTORY)
                .whereEqualTo(FIELD_EVENT_TYPE, EVENT_POMODORO_COMPLETED)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                //.get(Source.CACHE)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getProjectTaskCompletionHistory(@NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        List<String> list = new ArrayList<>();
        list.add(EVENT_PROJECT_COMPLETED);
        list.add(EVENT_TASK_COMPLETED);
        getCollection(COLLECTION_HISTORY)
                .whereIn(FIELD_EVENT_TYPE, list)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getProjectCompletionHistory(@NonNull String projectName,
                                                   @NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getCollection(COLLECTION_HISTORY)
                .whereEqualTo(FIELD_NAME, projectName)
                .whereEqualTo(FIELD_EVENT_TYPE, EVENT_PROJECT_COMPLETED)
                .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                //.get(Source.CACHE)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

}

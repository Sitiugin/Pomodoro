package com.glebworx.pomodoro.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.HistoryModel;
import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;


public class TaskApi extends BaseApi {

    //                                                                                     CONSTANTS


    //                                                                       CONSTRUCTOR SUPPRESSION

    private TaskApi() { }


    //                                                                                    PUBLIC API


    public static void addTask(@NonNull ProjectModel projectModel,
                               @NonNull TaskModel taskModel,
                               @Nullable OnCompleteListener<Void> onCompleteListener) {
        modifyTask(projectModel, taskModel, HistoryModel.EVENT_TASK_CREATED, onCompleteListener);
    }

    public static void updateTask(@NonNull ProjectModel projectModel,
                                  @NonNull TaskModel taskModel,
                                  @Nullable OnCompleteListener<Void> onCompleteListener) {
        modifyTask(projectModel, taskModel, HistoryModel.EVENT_TASK_UPDATED, onCompleteListener);
    }

    public static void completePomodoro(@NonNull ProjectModel projectModel,
                                        @NonNull TaskModel taskModel,
                                        @Nullable OnCompleteListener<Void> onCompleteListener) {
        //taskModel.addPomodoro(); // TODO handle failure
        //modifyTask(projectModel, taskModel, HistoryModel.EVENT_POMODORO_COMPLETED, onCompleteListener);

        projectModel.updateTimestamp();
        taskModel.updateTimestamp();

        taskModel.addPomodoro();
        projectModel.addPomodoro();

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.set(
                projectDocument.collection(COLLECTION_TASKS).document(taskModel.getName()),
                taskModel);

        batch.update(projectDocument,
                FIELD_TASKS, projectModel.getTasks(),
                FIELD_POMODOROS_ALLOCATED, projectModel.getPomodorosAllocated(),
                FIELD_POMODOROS_COMPLETED, projectModel.getPomodorosCompleted());

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), taskModel.getName(), HistoryModel.EVENT_POMODORO_COMPLETED));

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void completeTask(@NonNull ProjectModel projectModel,
                                    @NonNull TaskModel taskModel,
                                    @Nullable OnCompleteListener<Void> onCompleteListener) {
        /*taskModel.addPomodoro(); // TODO handle failure
        taskModel.complete();
        modifyTask(projectModel, taskModel, HistoryModel.EVENT_TASK_COMPLETED, onCompleteListener);*/

        projectModel.updateTimestamp();
        taskModel.updateTimestamp();

        taskModel.addPomodoro();
        projectModel.addPomodoro();
        taskModel.complete();

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.set(
                projectDocument.collection(COLLECTION_TASKS).document(taskModel.getName()),
                taskModel);

        batch.update(projectDocument,
                FIELD_TASKS, projectModel.getTasks(),
                FIELD_POMODOROS_ALLOCATED, projectModel.getPomodorosAllocated(),
                FIELD_POMODOROS_COMPLETED, projectModel.getPomodorosCompleted());

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), taskModel.getName(), HistoryModel.EVENT_POMODORO_COMPLETED));

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), taskModel.getName(), HistoryModel.EVENT_TASK_COMPLETED));

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void deleteTask(@NonNull ProjectModel projectModel,
                                  @NonNull TaskModel taskModel,
                                  @Nullable OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.delete(projectDocument.collection(COLLECTION_TASKS).document(taskModel.getName()));

        batch.update(projectDocument,
                FIELD_TASKS, projectModel.getTasks(),
                FIELD_POMODOROS_ALLOCATED, projectModel.getPomodorosAllocated(),
                FIELD_POMODOROS_COMPLETED, projectModel.getPomodorosCompleted());

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), taskModel.getName(), HistoryModel.EVENT_TASK_DELETED));

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

    public static void getTodayTasks(@NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        ZonedDateTime todayDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        getCollectionGroup(COLLECTION_TASKS)
                .whereEqualTo(FIELD_COMPLETED, false)
                .whereGreaterThanOrEqualTo(FIELD_DUE_DATE, Date.from(todayDateTime.toInstant()))
                .whereLessThan(FIELD_DUE_DATE, Date.from(todayDateTime.plusDays(1).toInstant()))
                .get(Source.CACHE)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getThisWeekTasks(@NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        ZonedDateTime todayDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        Date today = Date.from(todayDateTime.toInstant());
        Date inAWeek = Date.from(todayDateTime.plusDays(7).toInstant());
        getCollectionGroup(COLLECTION_TASKS)
                .whereEqualTo(FIELD_COMPLETED, false)
                .whereLessThanOrEqualTo(FIELD_DUE_DATE, inAWeek)
                .whereGreaterThanOrEqualTo(FIELD_DUE_DATE, today)
                .get(Source.CACHE)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void getOverdueTasks(@NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        getCollectionGroup(COLLECTION_TASKS)
                .whereEqualTo(FIELD_COMPLETED, false)
                .whereLessThan(FIELD_DUE_DATE, today)
                .get(Source.CACHE)
                .addOnCompleteListener(onCompleteListener);
    }

    public static void addAllTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        getCollectionGroup(COLLECTION_TASKS)
                .orderBy(FIELD_DUE_DATE, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    public static ListenerRegistration addTodayTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        ZonedDateTime todayDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        return getCollectionGroup(COLLECTION_TASKS)
                .whereGreaterThanOrEqualTo(FIELD_DUE_DATE, Date.from(todayDateTime.toInstant()))
                .whereLessThan(FIELD_DUE_DATE, Date.from(todayDateTime.plusDays(1).toInstant()))
                .addSnapshotListener(eventListener);
    }

    public static ListenerRegistration addThisWeekTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        ZonedDateTime todayDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        Date today = Date.from(todayDateTime.toInstant());
        Date inAWeek = Date.from(todayDateTime.plusDays(7).toInstant());
        return getCollectionGroup(COLLECTION_TASKS)
                .whereLessThanOrEqualTo(FIELD_DUE_DATE, inAWeek)
                .whereGreaterThanOrEqualTo(FIELD_DUE_DATE, today)
                .addSnapshotListener(eventListener);
    }

    public static ListenerRegistration addOverdueTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener) {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return getCollectionGroup(COLLECTION_TASKS)
                .whereLessThan(FIELD_DUE_DATE, today)
                .addSnapshotListener(eventListener);
    }

    public static ListenerRegistration addTodayTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener, boolean completed) {
        ZonedDateTime todayDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        return getCollectionGroup(COLLECTION_TASKS)
                .whereGreaterThanOrEqualTo(FIELD_DUE_DATE, Date.from(todayDateTime.toInstant()))
                .whereEqualTo(FIELD_COMPLETED, completed)
                .whereLessThan(FIELD_DUE_DATE, Date.from(todayDateTime.plusDays(1).toInstant()))
                .addSnapshotListener(eventListener);
    }

    public static ListenerRegistration addThisWeekTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener, boolean completed) {
        ZonedDateTime todayDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        Date today = Date.from(todayDateTime.toInstant());
        Date inAWeek = Date.from(todayDateTime.plusDays(7).toInstant());
        return getCollectionGroup(COLLECTION_TASKS)
                .whereLessThanOrEqualTo(FIELD_DUE_DATE, inAWeek)
                .whereGreaterThanOrEqualTo(FIELD_DUE_DATE, today)
                .whereEqualTo(FIELD_COMPLETED, completed)
                .addSnapshotListener(eventListener);
    }

    public static ListenerRegistration addOverdueTasksEventListener(@NonNull EventListener<QuerySnapshot> eventListener, boolean completed) {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return getCollectionGroup(COLLECTION_TASKS)
                .whereLessThan(FIELD_DUE_DATE, today)
                .whereEqualTo(FIELD_COMPLETED, completed)
                .addSnapshotListener(eventListener);
    }

    public static ListenerRegistration addTaskEventListener(@NonNull String projectName,
                                                            @NonNull EventListener<QuerySnapshot> eventListener,
                                                            boolean completed) {
        return getCollection(COLLECTION_PROJECTS)
                .document(projectName)
                .collection(COLLECTION_TASKS)
                .whereEqualTo(FIELD_COMPLETED, completed)
                //.orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    public static ListenerRegistration addSingleTaskEventListener(@NonNull String projectName,
                                                                  @NonNull String taskName,
                                                                  @NonNull EventListener<DocumentSnapshot> eventListener) {
        return getCollection(COLLECTION_PROJECTS)
                .document(projectName)
                .collection(COLLECTION_TASKS)
                .document(taskName)
                .addSnapshotListener(MetadataChanges.INCLUDE, eventListener);
    }

    private static void modifyTask(@NonNull ProjectModel projectModel,
                                   @NonNull TaskModel taskModel,
                                   @NonNull String eventType,
                                   @Nullable OnCompleteListener<Void> onCompleteListener) {

        projectModel.updateTimestamp();
        taskModel.updateTimestamp();

        WriteBatch batch = getWriteBatch();

        DocumentReference projectDocument = getCollection(COLLECTION_PROJECTS).document(projectModel.getName());

        batch.set(
                projectDocument.collection(COLLECTION_TASKS).document(taskModel.getName()),
                taskModel);

        batch.update(projectDocument,
                FIELD_TASKS, projectModel.getTasks(),
                FIELD_POMODOROS_ALLOCATED, projectModel.getPomodorosAllocated(),
                FIELD_POMODOROS_COMPLETED, projectModel.getPomodorosCompleted());

        batch.set(
                getCollection(COLLECTION_HISTORY).document(),
                new HistoryModel(projectModel.getName(), projectModel.getColorTag(), taskModel.getName(), eventType));

        if (onCompleteListener == null) {
            batch.commit();
        } else {
            batch.commit().addOnCompleteListener(onCompleteListener);
        }

    }

}

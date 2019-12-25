package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.util.UUID;

public class HistoryModel extends AbstractModel {


    //                                                                                     CONSTANTS

    public static final Parcelable.Creator<HistoryModel> CREATOR = new Parcelable.Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    public static final String EVENT_PROJECT_CREATED = "project_created";
    public static final String EVENT_PROJECT_UPDATED = "project_updated";
    public static final String EVENT_PROJECT_DELETED = "project_deleted";
    public static final String EVENT_PROJECT_COMPLETED = "project_completed";
    public static final String EVENT_PROJECT_RESTORED = "project_restored";

    public static final String EVENT_TASK_CREATED = "task_created";
    public static final String EVENT_TASK_UPDATED = "task_updated";
    public static final String EVENT_TASK_DELETED = "task_deleted";
    public static final String EVENT_POMODORO_COMPLETED = "pomodoro_completed";
    public static final String EVENT_TASK_COMPLETED = "task_completed";


    //                                                                                    ATTRIBUTES

    private final String id; // TODO why do we need id?
    private String colorTag;
    private String taskName;
    private String eventType;

    private int timeElapsed;


    //                                                                                  CONSTRUCTORS

    public HistoryModel() {
        super();
        this.id = generateStringId();
    }

    public HistoryModel(@NonNull TaskModel taskModel, @NonNull String eventType) {
        super();
        this.id = generateStringId();
    }

    public HistoryModel(@NonNull String projectName,
                        @Nullable String colorTag,
                        @Nullable String taskName,
                        @NonNull String eventType) {
        super(projectName);
        this.id = generateStringId();
        this.colorTag = colorTag;
        this.taskName = taskName;
        this.eventType = eventType;
    }

    public HistoryModel(@NonNull String projectName,
                        @Nullable String colorTag,
                        @Nullable String taskName,
                        @NonNull String eventType,
                        int timeElapsed) {
        super(projectName);
        this.id = generateStringId();
        this.colorTag = colorTag;
        this.taskName = taskName;
        this.eventType = eventType;
        this.timeElapsed = timeElapsed;
    }

    private HistoryModel(Parcel in) {
        super(in);
        this.id = in.readString();
        this.colorTag = in.readString();
        this.taskName = in.readString();
        this.eventType = in.readString();
        this.timeElapsed = in.readInt();
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(id);
        parcel.writeString(taskName);
        parcel.writeString(eventType);
        parcel.writeString(colorTag);
        parcel.writeInt(timeElapsed);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && eventType != null;
    }


    //                                                                           GETTERS AND SETTERS

    public String getId() {
        return id;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    //                                                                                       HELPERS

    private String generateStringId() {
        return UUID.randomUUID().toString();
    }

}

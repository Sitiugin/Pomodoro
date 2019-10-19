package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

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

    public static final String EVENT_TASK_CREATED = "task_created";
    public static final String EVENT_TASK_UPDATED = "task_updated";
    public static final String EVENT_TASK_DELETED = "task_deleted";
    public static final String EVENT_POMODORO_COMPLETED = "pomodoro_completed";
    public static final String EVENT_TASK_COMPLETED = "task_completed";


    //                                                                                    ATTRIBUTES

    private String colorTag;
    private String taskName;
    private String eventType;


    //                                                                                  CONSTRUCTORS

    public HistoryModel() {
        super();
    }

    public HistoryModel(@NonNull TaskModel taskModel, @NonNull String eventType) {
        super();
    }

    public HistoryModel(@NonNull String projectName,
                        @Nullable String colorTag,
                        @Nullable String taskName,
                        @NonNull String eventType) {
        super(projectName);
        this.colorTag = colorTag;
        this.taskName = taskName;
        this.eventType = eventType;
    }

    public HistoryModel(Parcel in) {
        super(in);
        this.colorTag = in.readString();
        this.taskName = in.readString();
        this.eventType = in.readString();
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(getTaskName());
        parcel.writeString(eventType);
        parcel.writeString(colorTag);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && eventType != null;
    }


    //                                                                           GETTERS AND SETTERS


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

}

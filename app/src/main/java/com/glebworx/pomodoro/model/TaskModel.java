package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class TaskModel extends AbstractModel {


    //                                                                                     CONSTANTS

    public static final Parcelable.Creator<TaskModel> CREATOR = new Parcelable.Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };


    //                                                                                    ATTRIBUTES

    @SerializedName("projectName")
    private final String projectName;
    @SerializedName("pomodorosAllocated")
    private int pomodorosAllocated;
    @SerializedName("pomodorosCompleted")
    private int pomodorosCompleted;
    @SerializedName("timeElapsed")
    private int timeElapsed;
    @SerializedName("dueDate")
    private Date dueDate;
    @SerializedName("completed")
    private boolean completed;


    //                                                                                  CONSTRUCTORS

    public TaskModel() {
        super();
        this.projectName = null;
        this.pomodorosAllocated = 0;
        this.pomodorosCompleted = 0;
        this.timeElapsed = 0;
        this.dueDate = new Date();
        this.completed = false;
    }

    public TaskModel(String projectName) {
        super();
        this.projectName = projectName;
        this.pomodorosAllocated = 0;
        this.pomodorosCompleted = 0;
        this.timeElapsed = 0;
        this.dueDate = new Date();
        this.completed = false;
    }

    public TaskModel(@NonNull String name,
                     @NonNull String projectName,
                     int pomodorosAllocated,
                     @Nullable Date dueDate,
                     boolean completed) {
        super(name);
        this.projectName = projectName;
        this.pomodorosAllocated = pomodorosAllocated;
        this.pomodorosCompleted = 0;
        this.timeElapsed = 0;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    public TaskModel(Parcel in) {
        super(in);
        this.projectName = in.readString();
        this.pomodorosAllocated = in.readInt();
        this.pomodorosCompleted = in.readInt();
        this.timeElapsed = in.readInt();
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.completed = in.readInt() == 1;
    }

    public TaskModel(@NonNull TaskModel taskModel) {
        this(
                taskModel.getName(),
                taskModel.getProjectName(),
                taskModel.getPomodorosAllocated(),
                taskModel.getDueDate(),
                taskModel.isCompleted());
        this.pomodorosCompleted = taskModel.getPomodorosCompleted();
        this.timeElapsed = taskModel.getTimeElapsed();
        this.completed = taskModel.completed;
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.projectName);
        parcel.writeInt(this.pomodorosAllocated);
        parcel.writeInt(this.pomodorosCompleted);
        parcel.writeInt(this.timeElapsed);
        if (this.dueDate != null) {
            parcel.writeLong(this.dueDate.getTime());
        } else {
            parcel.writeLong(-1);
        }
        parcel.writeInt(completed ? 1 : 0);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && projectName != null && dueDate != null;
    }


    //                                                                           GETTERS AND SETTERS

    public String getProjectName() {
        return projectName;
    }

    public int getPomodorosAllocated() {
        return pomodorosAllocated;
    }

    public void setPomodorosAllocated(int pomodorosAllocated) {
        this.pomodorosAllocated = pomodorosAllocated;
    }

    public int getPomodorosCompleted() {
        return pomodorosCompleted;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isOverdue() {
        return new Date().compareTo(dueDate) > 0;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }

    @Exclude
    public void addPomodoro(int timeElapsed) {
        pomodorosCompleted++;
        this.timeElapsed += timeElapsed;
    }

    @Exclude
    public boolean isOverLimit() {
        return pomodorosCompleted >= pomodorosAllocated && !completed;
    }

}

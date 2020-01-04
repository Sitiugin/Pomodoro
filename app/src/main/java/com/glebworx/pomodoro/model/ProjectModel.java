package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.util.Date;


public class ProjectModel extends AbstractModel {


    //                                                                                     CONSTANTS

    public static final Parcelable.Creator<ProjectModel> CREATOR = new Parcelable.Creator<ProjectModel>() {
        @Override
        public ProjectModel createFromParcel(Parcel in) {
            return new ProjectModel(in);
        }

        @Override
        public ProjectModel[] newArray(int size) {
            return new ProjectModel[size];
        }
    };


    //                                                                                    ATTRIBUTES

    private Date dueDate;
    private String colorTag;
    private int estimatedTime;
    private int elapsedTime;
    private float progress;
    private boolean allTasksCompleted;
    private boolean completed;
    private Date completedOn;



    //                                                                                  CONSTRUCTORS

    public ProjectModel() {
        super();
        this.dueDate = new Date();
        this.colorTag = null;
        this.estimatedTime = 0;
        this.elapsedTime = 0;
        this.allTasksCompleted = true;
        this.completed = false;
        this.completedOn = null;
    }

    public ProjectModel(@NonNull String name,
                        @Nullable Date dueDate,
                        @Nullable String colorTag) {
        super(name);
        this.dueDate = dueDate;
        this.colorTag = colorTag;
        this.estimatedTime = 0;
        this.elapsedTime = 0;
        this.progress = 0;
        this.allTasksCompleted = true;
        this.completed = false;
        this.completedOn = null;
    }

    public ProjectModel(ProjectModel projectModel) {
        super(projectModel.getName());
        dueDate = projectModel.getDueDate();
        colorTag = projectModel.getColorTag();
        estimatedTime = projectModel.getEstimatedTime();
        elapsedTime = projectModel.getElapsedTime();
        progress = projectModel.getProgress();
        allTasksCompleted = projectModel.getAllTasksCompleted();
        completed = projectModel.isCompleted();
        completedOn = projectModel.getCompletedOn();
    }

    public ProjectModel(Parcel in) {
        super(in);
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.colorTag = in.readString();
        this.estimatedTime = in.readInt();
        this.elapsedTime = in.readInt();
        this.progress = in.readFloat();
        this.allTasksCompleted = in.readInt() == 1;
        this.completed = in.readInt() == 1;
        long completedOn = in.readLong();
        if (completedOn != -1) {
            this.completedOn = new Date(completedOn);
        }
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        if (dueDate != null) {
            parcel.writeLong(dueDate.getTime());
        } else {
            parcel.writeLong(-1);
        }
        parcel.writeString(colorTag);
        parcel.writeInt(estimatedTime);
        parcel.writeInt(elapsedTime);
        parcel.writeFloat(progress);
        parcel.writeInt(allTasksCompleted ? 1 : 0);
        parcel.writeInt(completed ? 1 : 0);
        if (completedOn != null) {
            parcel.writeLong(completedOn.getTime());
        } else {
            parcel.writeLong(-1);
        }
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && dueDate != null;
    }


    //                                                                                     INTERFACE

    public void updateFromModel(ProjectModel projectModel) {
        setName(projectModel.getName());
        updateTimestamp();
        dueDate = projectModel.getDueDate();
        colorTag = projectModel.getColorTag();
        estimatedTime = projectModel.getEstimatedTime();
        elapsedTime = projectModel.getElapsedTime();
        progress = projectModel.getProgress();
        allTasksCompleted = projectModel.getAllTasksCompleted();
        completed = projectModel.isCompleted();
        completedOn = projectModel.getCompletedOn();
    }


    //                                                                           GETTERS AND SETTERS

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(int estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public boolean getAllTasksCompleted() {
        return allTasksCompleted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Date getCompletedOn() {
        return completedOn;
    }

    public void setCompletedOn(Date completedOn) {
        this.completedOn = completedOn;
    }

}

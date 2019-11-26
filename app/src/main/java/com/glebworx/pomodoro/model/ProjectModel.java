package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    private List<String> tasks;
    private int estimatedTime;
    private int elapsedTime;
    private float progress;
    private boolean isCompleted;



    //                                                                                  CONSTRUCTORS

    public ProjectModel() {
        super();
        this.tasks = new ArrayList<>();
    }

    public ProjectModel(@NonNull String name,
                        @Nullable Date dueDate,
                        @Nullable String colorTag,
                        @Nullable String layout) {
        super(name);
        this.dueDate = dueDate;
        this.colorTag = colorTag;
        this.tasks = new ArrayList<>();
        this.estimatedTime = 0;
        this.elapsedTime = 0;
        this.progress = 0;
        this.isCompleted = false;
    }

    public ProjectModel(ProjectModel projectModel) {
        super(projectModel.getName());
        dueDate = projectModel.getDueDate();
        colorTag = projectModel.getColorTag();
        tasks = projectModel.getTasks();
        estimatedTime = projectModel.getEstimatedTime();
        elapsedTime = projectModel.getElapsedTime();
        progress = projectModel.getProgress();
        isCompleted = projectModel.isCompleted();
    }

    public ProjectModel(Parcel in) {
        super(in);
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.colorTag = in.readString();
        in.readStringList(this.tasks);
        this.estimatedTime = in.readInt();
        this.elapsedTime = in.readInt();
        this.progress = in.readFloat();
        this.isCompleted = in.readInt() == 1;
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
        parcel.writeStringList(tasks);
        parcel.writeInt(estimatedTime);
        parcel.writeInt(elapsedTime);
        parcel.writeFloat(progress);
        parcel.writeInt(isCompleted ? 1 : 0);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && dueDate != null;
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

    public List<String> getTasks() {
        return tasks;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public float getProgress() {
        return progress;
    }

}

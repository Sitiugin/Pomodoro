package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.item.TaskItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


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
    //@Exclude
    //private Map<String, TaskModel> tasks; //  TODO remove

    private List<String> tasks;
    private int pomodorosAllocated;
    private int pomodorosCompleted;



    //                                                                                  CONSTRUCTORS

    public ProjectModel() {
        super();
        this.tasks = new ArrayList<>();
    }

    public ProjectModel(@NonNull String name,
                        @Nullable Date dueDate,
                        @NonNull String colorTag) {
        super(name);
        if (dueDate != null) {
            this.dueDate = dueDate;
        } else {
            this.dueDate = null;
        }
        this.colorTag = colorTag;
        this.tasks = new ArrayList<>();
        this.pomodorosAllocated = 0;
        this.pomodorosCompleted = 0;
    }

    public ProjectModel(Parcel in) {
        super(in);
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.colorTag = in.readString();
        in.readStringList(this.tasks);
        this.pomodorosAllocated = in.readInt();
        this.pomodorosCompleted = in.readInt();
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
        parcel.writeInt(pomodorosAllocated);
        parcel.writeInt(pomodorosCompleted);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && colorTag != null && dueDate != null;
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

    @Exclude
    public void addTask(TaskModel taskModel) {
        tasks.add(taskModel.getName());
        pomodorosAllocated += taskModel.getPomodorosAllocated();
        pomodorosCompleted += taskModel.getPomodorosCompleted();
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

    public void setPomodorosCompleted(int pomodorosCompleted) {
        this.pomodorosCompleted = pomodorosCompleted;
    }

    @Exclude
    public double getProgressRatio() {
        if (pomodorosAllocated == 0) {
            return 0;
        }
        return (double) pomodorosCompleted / pomodorosAllocated;
    }

}

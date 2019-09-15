package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


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

    private String name;
    private Date dueDate;
    private String colorTag;
    private List<TaskModel> tasks;


    //                                                                                  CONSTRUCTORS

    public ProjectModel() {
        super();
        this.tasks = new ArrayList<>();
    }

    public ProjectModel(@NonNull String name,
                        @Nullable Date dueDate,
                        @NonNull String colorTag,
                        @Nullable List<TaskModel> tasks) {
        super();
        this.name = name;
        if (dueDate != null) {
            this.dueDate = dueDate;
        } else {
            this.dueDate = null;
        }
        this.colorTag = colorTag;
        if (tasks != null) {
            this.tasks = tasks;
        } else {
            this.tasks = new ArrayList<>();
        }
    }

    public ProjectModel(Parcel in) {
        super(in);
        this.name = in.readString();
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.colorTag = in.readString();
        this.tasks = new ArrayList<>();
        in.readTypedList(this.tasks, TaskModel.CREATOR);
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.name);
        if (this.dueDate != null) {
            parcel.writeLong(this.dueDate.getTime());
        } else {
            parcel.writeLong(-1);
        }
        parcel.writeString(this.colorTag);
        parcel.writeTypedList(this.tasks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectModel)) return false;
        ProjectModel that = (ProjectModel) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return name != null && colorTag != null && tasks != null;
    }


    //                                                                           GETTERS AND SETTERS


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public List<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }

    @Exclude
    public double getProgressRatio() {
        int allocated = 0;
        int completed = 0;
        for (int i = 0; i < tasks.size(); i++) {
            allocated += tasks.get(i).getPomodorosAllocated();
            completed += tasks.get(i).getPomodorosCompleted();
        }
        if (allocated == 0) {
            return 0;
        }
        return (double) completed / (double) allocated;
    }

}

package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

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
    private Map<String, TaskModel> tasks;


    //                                                                                  CONSTRUCTORS

    public ProjectModel() {
        super();
        this.tasks = new HashMap<>();
    }

    public ProjectModel(@NonNull String name,
                        @Nullable Date dueDate,
                        @NonNull String colorTag,
                        @Nullable Map<String, TaskModel> tasks) {
        super(name);
        if (dueDate != null) {
            this.dueDate = dueDate;
        } else {
            this.dueDate = null;
        }
        this.colorTag = colorTag;
        if (tasks != null) {
            this.tasks = tasks;
        } else {
            this.tasks = new HashMap<>();
        }
    }

    public ProjectModel(Parcel in) {
        super(in);
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.colorTag = in.readString();
        this.tasks = new HashMap<>();
        in.readMap(this.tasks, HashMap.class.getClassLoader());
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        if (this.dueDate != null) {
            parcel.writeLong(this.dueDate.getTime());
        } else {
            parcel.writeLong(-1);
        }
        parcel.writeString(this.colorTag);
        parcel.writeMap(this.tasks);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return getName() != null && colorTag != null && tasks != null;
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

    public Map<String, TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<String, TaskModel> tasks) {
        this.tasks = tasks;
    }

    @Exclude
    public void addTask(TaskModel taskModel) {
        tasks.put(taskModel.getName(), taskModel);
    }

    @Exclude
    public void removeTask(TaskModel taskModel) {
        tasks.remove(taskModel.getName());
    }


    @Exclude
    public double getProgressRatio() {
        int allocated = 0;
        int completed = 0;
        Set<Map.Entry<String, TaskModel>> entrySet = tasks.entrySet();
        Iterator<Map.Entry<String, TaskModel>> iterator =  entrySet.iterator();
        Map.Entry<String, TaskModel> next;
        while (iterator.hasNext()) {
            next = iterator.next();
            allocated += next.getValue().getPomodorosAllocated();
            completed += next.getValue().getPomodorosCompleted();
        }
        if (allocated == 0) {
            return 0;
        }
        return (double) completed / (double) allocated;
    }

    @Exclude
    public Map<String, Object> getTasksAsMap() {
        Map<String, Object> map = new HashMap<>();
        Set<Map.Entry<String, TaskModel>> entrySet = tasks.entrySet();
        for (Map.Entry<String, TaskModel> entry: entrySet) {
            map.put(entry.getKey(), entry.getValue().getTaskAsMap());
        }
        return map;
    }

}

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

    /*private List<String> tasks;
    private int pomodorosAllocated;
    private int pomodorosCompleted;*/



    //                                                                                  CONSTRUCTORS

    public ProjectModel() {
        super();
        //this.tasks = new HashMap<>();
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
        /*if (tasks != null) {
            this.tasks = tasks;
        } else {
            this.tasks = new HashMap<>();
        }*/
    }

    public ProjectModel(Parcel in) {
        super(in);
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.colorTag = in.readString();
        /*this.tasks = new HashMap<>();
        in.readMap(this.tasks, HashMap.class.getClassLoader());*/
    }

    /*public ProjectModel(@NonNull DocumentSnapshot snapshot) {
        this();
        if (snapshot.exists()) {
            Map<String, Object> map = snapshot.getData();
            if (map == null) {
                return;
            }
            try {
                setName((String) map.get("name"));
                updateTimestamp();
                dueDate = (Date) map.get("dueDate");
                colorTag = (String) map.get("colorTag");
                Map<String, Map<String, Object>> tasks = (Map<String, Map<String, Object>>) snapshot.get("tasks");
                if (tasks == null) {
                    return;
                }
                Set<Map.Entry<String, Map<String, Object>>> entrySet = tasks.entrySet();
                TaskModel taskModel;
                for (Map.Entry<String, Map<String, Object>> entry: entrySet) {
                    taskModel = new TaskModel(entry.getValue());
                    addTask(taskModel);
                }
            } catch (ClassCastException ignored) { }
        }

    }*/


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
        //parcel.writeMap(this.tasks);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return getName() != null && colorTag != null;
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

    /*@Exclude
    public Map<String, TaskModel> getTasks() {
        return tasks;
    }

    @Exclude
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
    }*/


    @Exclude
    public double getProgressRatio() {
        return 0;
        /*int allocated = 0;
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
        return (double) completed / (double) allocated;*/
    }

    /*@Exclude
    public Map<String, Object> getTasksAsMap() {
        Map<String, Object> map = new HashMap<>();
        Set<Map.Entry<String, TaskModel>> entrySet = tasks.entrySet();
        for (Map.Entry<String, TaskModel> entry: entrySet) {
            map.put(entry.getKey(), entry.getValue().getTaskAsMap());
        }
        return map;
    }*/

}

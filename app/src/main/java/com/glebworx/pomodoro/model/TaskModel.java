package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

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

    public static final String RECURRENCE_EVERY_DAY = "EVERY_DAY";
    public static final String RECURRENCE_EVERY_TWO_DAYS = "EVERY_TWO_DAYS";
    public static final String RECURRENCE_EVERY_THREE_DAYS = "EVERY_THREE_DAYS";
    public static final String RECURRENCE_EVERY_FOUR_DAYS = "FOUR_DAYS";
    public static final String RECURRENCE_EVERY_FIVE_DAYS = "FIVE_DAYS";
    public static final String RECURRENCE_EVERY_SIX_DAYS = "SIX_DAYS";
    public static final String RECURRENCE_EVERY_WEEKLY = "EVERY_WEEK";
    public static final String RECURRENCE_WEEKDAY = "EVERY_WEEKDAY";
    public static final String RECURRENCE_WEEKEND = "EVERY_WEEKEND";
    public static final String RECURRENCE_MONTHLY = "EVERY_MONTH";


    //                                                                                    ATTRIBUTES

    private final String projectName;
    private int pomodorosAllocated;
    private int pomodorosCompleted;
    private Date dueDate;
    private String section;
    private String recurrence;
    private boolean completed;


    //                                                                                  CONSTRUCTORS

    public TaskModel() {
        super();
        this.projectName = null;
    }

    public TaskModel(String projectName) {
        super();
        this.projectName = projectName;
    }

    public TaskModel(@NonNull String name,
                     @NonNull String projectName,
                     int pomodorosAllocated,
                     @Nullable Date dueDate,
                     @Nullable String section,
                     @Nullable String recurrence,
                     boolean completed) {
        super(name);
        this.projectName = projectName;
        this.pomodorosAllocated = pomodorosAllocated;
        this.pomodorosCompleted = 0;
        this.dueDate = dueDate;
        this.section = section;
        this.recurrence = recurrence;
        this.completed = completed;
    }

    public TaskModel(Parcel in) {
        super(in);
        this.projectName = in.readString();
        this.pomodorosAllocated = in.readInt();
        this.pomodorosCompleted = in.readInt();
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.section = in.readString();
        this.recurrence = in.readString();
        this.completed = in.readInt() == 1;
    }

    public TaskModel(@NonNull TaskModel taskModel) {
        this(
                taskModel.getName(),
                taskModel.getProjectName(),
                taskModel.getPomodorosAllocated(),
                taskModel.getDueDate(),
                taskModel.getSection(),
                taskModel.getRecurrence(),
                taskModel.isCompleted());
        this.pomodorosCompleted = taskModel.getPomodorosCompleted();
        this.completed = taskModel.completed;
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(this.projectName);
        parcel.writeInt(this.pomodorosAllocated);
        parcel.writeInt(this.pomodorosCompleted);
        if (this.dueDate != null) {
            parcel.writeLong(this.dueDate.getTime());
        } else {
            parcel.writeLong(-1);
        }
        parcel.writeString(section);
        parcel.writeString(recurrence);
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

    public void setPomodorosCompleted(int pomodorosCompleted) {
        this.pomodorosCompleted = pomodorosCompleted;
    }

    @Exclude
    public void addPomodoro() {
        pomodorosCompleted++;
    }

    @Exclude
    public boolean isOverLimit() {
        return pomodorosCompleted >= pomodorosAllocated && !completed;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }

}

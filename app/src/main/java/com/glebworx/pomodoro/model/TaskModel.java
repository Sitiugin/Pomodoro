package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


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

    private int pomodorosAllocated;
    private int pomodorosCompleted;
    private Date dueDate;
    private String section;
    private String recurrence;
    private boolean isCompleted;


    //                                                                                  CONSTRUCTORS

    public TaskModel() {
        super();
    }

    public TaskModel(@NonNull String name,
                     int pomodorosAllocated,
                     @Nullable Date dueDate,
                     @Nullable String section,
                     @Nullable String recurrence) {
        super(name);
        this.pomodorosAllocated = pomodorosAllocated;
        this.pomodorosCompleted = 0;
        if (dueDate != null) {
            this.dueDate = dueDate;
        } else {
            this.dueDate = null;
        }
        if (section != null) {
            this.section = section;
        } else {
            this.section = null;
        }
        if (recurrence != null) {
            this.recurrence = recurrence;
        } else {
            this.recurrence = null;
        }
        this.isCompleted = false;
    }

    public TaskModel(Parcel in) {
        super(in);
        this.pomodorosAllocated = in.readInt();
        this.pomodorosCompleted = in.readInt();
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.section = in.readString();
        this.recurrence = in.readString();
        this.isCompleted = in.readInt() == 1;
    }

    public TaskModel(@NonNull TaskModel taskModel) {
        this(taskModel.getName(),
        taskModel.getPomodorosAllocated(),
        taskModel.getDueDate(),
        taskModel.getSection(),
        taskModel.getRecurrence());
        this.pomodorosCompleted = taskModel.getPomodorosCompleted();
        this.isCompleted = taskModel.isCompleted;
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(this.pomodorosAllocated);
        parcel.writeInt(this.pomodorosCompleted);
        if (this.dueDate != null) {
            parcel.writeLong(this.dueDate.getTime());
        } else {
            parcel.writeLong(-1);
        }
        parcel.writeString(section);
        parcel.writeString(recurrence);
        parcel.writeInt(isCompleted ? 1 : 0);
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && dueDate != null;
    }


    //                                                                           GETTERS AND SETTERS

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
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

}

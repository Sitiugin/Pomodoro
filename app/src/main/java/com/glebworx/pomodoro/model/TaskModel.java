package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
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


    //                                                                                    ATTRIBUTES

    private int pomodorosAllocated;
    private int pomodorosCompleted;
    private Date dueDate;
    private int recurrence;


    //                                                                                  CONSTRUCTORS

    public TaskModel() {
        super();
    }

    public TaskModel(@NonNull String name,
                     int pomodorosAllocated,
                     @Nullable Date dueDate,
                     int recurrence) {
        super(name);
        this.pomodorosAllocated = pomodorosAllocated;
        this.pomodorosCompleted = 0;
        if (dueDate != null) {
            this.dueDate = dueDate;
        } else {
            this.dueDate = null;
        }
        this.recurrence = recurrence;
    }

    public TaskModel(Parcel in) {
        super(in);
        this.pomodorosAllocated = in.readInt();
        this.pomodorosCompleted = in.readInt();
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.recurrence = in.readInt();
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
        parcel.writeInt(recurrence);
    }

    @Override
    public boolean isValid() {
        return getName() != null;
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

    public int getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(int recurrence) {
        this.recurrence = recurrence;
    }
}

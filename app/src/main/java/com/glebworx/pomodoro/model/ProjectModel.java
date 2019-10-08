package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static com.glebworx.pomodoro.util.manager.DateTimeManager.POMODORO_LENGTH;


public class ProjectModel extends AbstractModel {


    //                                                                                     CONSTANTS

    public static final String LAYOUT_LIST = "list";
    public static final String LAYOUT_BOARD = "board";
    public static final String LAYOUT_CALENDAR = "calendar";

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
    private List<String> sections;
    private List<String> tasks;
    private int pomodorosAllocated;
    private int pomodorosCompleted;
    private int elapsedTime;
    private String layout;
    private boolean isCompleted;



    //                                                                                  CONSTRUCTORS

    public ProjectModel() {
        super();
        this.tasks = new ArrayList<>();
        this.sections = new ArrayList<>();
        this.layout = LAYOUT_LIST;
    }

    public ProjectModel(@NonNull String name,
                        @Nullable Date dueDate,
                        @Nullable String colorTag,
                        @Nullable String layout) {
        super(name);
        if (dueDate != null) {
            this.dueDate = dueDate;
        } else {
            this.dueDate = null;
        }
        this.colorTag = colorTag;
        this.tasks = new ArrayList<>();
        this.sections = new ArrayList<>();
        this.pomodorosAllocated = 0;
        this.pomodorosCompleted = 0;
        this.elapsedTime = 0;
        if (this.layout != null) {
            this.layout = layout;
        } else {
            this.layout = LAYOUT_LIST;
        }
        this.isCompleted = false;
    }

    public ProjectModel(Parcel in) {
        super(in);
        long dueDate = in.readLong();
        if (dueDate != -1) {
            this.dueDate = new Date(dueDate);
        }
        this.colorTag = in.readString();
        in.readStringList(this.tasks);
        in.readStringList(this.sections);
        this.pomodorosAllocated = in.readInt();
        this.pomodorosCompleted = in.readInt();
        this.elapsedTime = in.readInt();
        this.layout = in.readString();
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
        parcel.writeStringList(sections);
        parcel.writeInt(pomodorosAllocated);
        parcel.writeInt(pomodorosCompleted);
        parcel.writeInt(elapsedTime);
        parcel.writeString(layout);
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

    @Exclude
    public void addTask(TaskModel taskModel) {
        if (!sections.contains(taskModel.getSection())) {
            sections.add(taskModel.getSection());
        }
        tasks.add(taskModel.getName());
        pomodorosAllocated += taskModel.getPomodorosAllocated();
        pomodorosCompleted += taskModel.getPomodorosCompleted();
    }

    @Exclude
    public void setTask(TaskModel oldTaskModel, TaskModel taskModel) {
        this.pomodorosAllocated -= oldTaskModel.getPomodorosAllocated();
        this.pomodorosCompleted -= oldTaskModel.getPomodorosCompleted();
        this.pomodorosAllocated += taskModel.getPomodorosAllocated();
        this.pomodorosCompleted += taskModel.getPomodorosCompleted();
    }

    @Exclude
    private int getTaskItemIndex(@NonNull String name) {
        return IntStream.range(0, tasks.size())
                .filter(i -> name.equals(tasks.get(i)))
                .findFirst().orElse(-1);
    }

    @Exclude
    public void removeTask(TaskModel taskModel) {
        tasks.remove(taskModel.getName());
        pomodorosAllocated -= taskModel.getPomodorosAllocated();
        pomodorosCompleted -= taskModel.getPomodorosCompleted();
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
    public int getEstimatedTime() {
        return pomodorosAllocated * POMODORO_LENGTH;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    @Exclude
    public void addElapsedTime(int elapsedTime) {
        this.elapsedTime += elapsedTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Exclude
    public double getProgressRatio() {
        if (pomodorosAllocated == 0) {
            return 0;
        }
        return (double) pomodorosCompleted / pomodorosAllocated;
    }

}

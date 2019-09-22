package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractModel implements Comparable<AbstractModel>, Parcelable {


    //                                                                                    ATTRIBUTES

    private String name;
    private Date timestamp;


    //                                                                                  CONSTRUCTORS

    public AbstractModel() {
        this.name = null;
        this.timestamp = new Date();
    }

    public AbstractModel(@NonNull String name) {
        this.name = name;
        this.timestamp = new Date();
    }

    public AbstractModel(Parcel in) {
        this.name = in.readString();
        this.timestamp = new Date(in.readLong());
    }


    //                                                                                    OVERRIDDEN


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractModel)) return false;
        AbstractModel that = (AbstractModel) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timestamp);
    }

    @Override
    public int compareTo(@NonNull AbstractModel other) {
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeLong(timestamp.getTime());
    }


    //                                                                                       METHODS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Exclude
    public void updateTimestamp() {
        timestamp = new Date();
    }

    @Exclude
    public boolean isValid() {
        return name != null && !(name.contains(".")
                || name.contains("$")
                || name.contains("[")
                || name.contains("]")
                || name.contains("#")
                || name.contains("/")); // TODO encode/decode instead
    }

}
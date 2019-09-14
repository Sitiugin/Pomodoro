package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractModel implements Comparable<AbstractModel>, Parcelable {


    //                                                                                    ATTRIBUTES

    private final String id;
    private final Date timestamp;
    private Date dateModified;


    //                                                                                  CONSTRUCTORS

    public AbstractModel() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = new Date();
        this.dateModified = new Date();
    }

    public AbstractModel(Parcel in) {
        this.id = in.readString();
        this.timestamp = new Date(in.readLong());
        this.dateModified = new Date(in.readLong());
    }


    //                                                                                    OVERRIDDEN


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractModel)) return false;
        AbstractModel that = (AbstractModel) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(@NonNull AbstractModel other) {
        return this.dateModified.compareTo(other.dateModified);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.id);
        parcel.writeLong(this.timestamp.getTime());
        parcel.writeLong(this.dateModified.getTime());
    }


    //                                                                                       METHODS

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public Date getTimestamp() {
        return timestamp;
    }

    @Exclude
    public Date getDateModified() {
        return dateModified;
    }

    @Exclude
    public void updateDateModified() {
        this.dateModified = new Date();
    }

    public abstract boolean isValid();

}
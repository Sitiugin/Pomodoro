package com.glebworx.pomodoro.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class HistoryModel extends AbstractModel {

    //                                                                                     CONSTANTS

    public static final Parcelable.Creator<HistoryModel> CREATOR = new Parcelable.Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };


    //                                                                                    ATTRIBUTES

    private String projectName;


    //                                                                                  CONSTRUCTORS

    public HistoryModel() {
        super();
    }

    public HistoryModel(@NonNull String projectName, @NonNull String taskName) {
        super(taskName);
        this.projectName = projectName;
    }

    public HistoryModel(Parcel in) {
        super(in);
        this.projectName = in.readString();
    }


    //                                                                                    OVERRIDDEN

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(getName());
    }

    @Exclude
    @Override
    public boolean isValid() {
        return super.isValid() && projectName != null;
    }

}

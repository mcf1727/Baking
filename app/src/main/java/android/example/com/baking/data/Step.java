/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable {
    private int id;
    private String shortDescription;
    private String description;
    private String videoURL;

    public Step() {}

    public int getId() { return id; }
    public String getShortDescription() { return shortDescription; }
    public String getDescription() { return description; }
    public String getVideoURL() { return videoURL; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(videoURL);
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel parcel) {
            Step step = new Step();
            step.id = parcel.readInt();
            step.shortDescription = parcel.readString();
            step.description = parcel.readString();
            step.videoURL = parcel.readString();

            return step;
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}

package com.insomniac.eventmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sanjeev on 1/17/2018.
 */

public class Event implements Parcelable{

    public String name;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String Id;

    public String getName() {
        return name;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event();
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String type;
    public String place;
    public String startTime;
    public String endTime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.place);
        parcel.writeString(this.type);
        parcel.writeString(this.endTime);
        parcel.writeString(this.startTime);
        parcel.writeString(this.Id);
    }
}

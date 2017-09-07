package com.iwitness.androidapp.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Notifications implements Parcelable {

    public String notificationName;
    public String userId;
    public String Readstatus;
    boolean selected = false;
    public int Number = 0;

    public Notifications() {
        super();
    }

    protected Notifications(Parcel in) {
        notificationName = in.readString();
        userId = in.readString();
        Readstatus = in.readString();
        selected = in.readByte() != 0;
        Number = in.readInt();
    }

    public static final Creator<Notifications> CREATOR = new Creator<Notifications>() {
        @Override
        public Notifications createFromParcel(Parcel in) {
            return new Notifications(in);
        }

        @Override
        public Notifications[] newArray(int size) {
            return new Notifications[size];
        }
    };

    public void setnotificationName(String notificationName) {
        this.notificationName = notificationName != null ? notificationName : "";
    }

    public void setuserId(String userId) {
        this.userId = userId != null ? userId : "";
    }
    public void setstatus(String status) {
        this.Readstatus = status != null ? status : "";
    }
    public void setNumber(int Number) {
        this.Number = Number != 0? Number : 0;
    }
    public String getnotificationName() {
        return this.notificationName;
    }
    public int getNumber() {
        return Number;
    }
    public String getstatus() {
        return this.Readstatus;
    }
    public String getuserId() {
        return this.userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(userId);
        dest.writeString(notificationName);
        dest.writeInt(Number);
        dest.writeString(Readstatus);

    }
}

package com.iwitness.androidapp.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Contact implements Parcelable {

    private String contactName;
    private String contactFirstName;
    private String contactLastName;
    private String contactNumber =null;
    private String number =null;
    private Bitmap contactPhoto;
    private Uri contactPhotoUri;
    private String contactEmail;

    boolean selected = false;

    public Contact() {
        super();
    }

    public void setContactFirstName(String firstName) {
        this.contactFirstName = firstName != null ? firstName : "";
    }

    public void setContactLastName(String lastName) {
        this.contactLastName = lastName != null ? lastName : "";
    }

    public String getContactFirstName() {
        return this.contactFirstName;
    }

    public String getContactLastName() {
        return this.contactLastName;
    }

    public String getContactName() {
        return String.format("%s %s", this.contactFirstName, this.contactLastName);
    }

    public String getContactNumber() {
        if (contactNumber != null && !contactNumber.isEmpty()) {
            return contactNumber.replaceAll("[- ]", "");
        }

        return contactNumber;
    }

    public Bitmap getContactPhoto() {
        return contactPhoto;
    }

    public Uri getContactPhotoUri() {
        return contactPhotoUri;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setSelectedNumber(String contactNumber) {
        Log.e("setSelectedNumber", "TEST" + contactNumber);
        this.number = contactNumber;
    }

    public String getNumber() {
        if (number != null && !number.isEmpty()) {
            return number.replaceAll("[- ]", "");
        }

        return number;
    }

    public void setContactPhoto(Bitmap contactPhoto) {
        this.contactPhoto = contactPhoto;

    }

    public void setContactPhotoUri(Uri contactPhotoUri) {
        this.contactPhotoUri = contactPhotoUri;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", this.contactFirstName, this.contactLastName, contactNumber);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(contactName);
        dest.writeString(number);
        dest.writeString(contactNumber);
        dest.writeString(contactPhotoUri.toString());
        dest.writeString(contactEmail);

    }

    public Contact(Parcel source) {

        contactName = source.readString();
        contactNumber = source.readString();
        number= source.readString();
        contactPhotoUri = Uri.parse(source.readString());
        contactEmail = source.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }

    };

}

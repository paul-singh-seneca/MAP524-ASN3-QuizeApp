package com.example.quizeapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UserAttempts implements Parcelable {
    private List<Boolean> attempts;

    public UserAttempts() {
        attempts = new ArrayList<>();
    }

    public void addAttempt(boolean isCorrect) {
        attempts.add(isCorrect);
    }

    public List<Boolean> getAttempts() {
        return attempts;
    }

    // Parcelable creator
    public static final Creator<UserAttempts> CREATOR = new Creator<UserAttempts>() {
        @Override
        public UserAttempts createFromParcel(Parcel in) {
            return new UserAttempts(in);
        }

        @Override
        public UserAttempts[] newArray(int size) {
            return new UserAttempts[size];
        }
    };

    // Constructor that takes a Parcel and initializes the object from it
    protected UserAttempts(Parcel in) {
        attempts = new ArrayList<>();
        in.readList(attempts, Boolean.class.getClassLoader());
    }

    // Write object data to the parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(attempts);
    }

    // Describe the kind of special object
    // This is required, but you can use 0 if there are no special objects
    @Override
    public int describeContents() {
        return 0;
    }
}

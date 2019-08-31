package com.example.williamstest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;
import java.io.Serializable;
import java.util.ArrayList;

public class Cornice implements Serializable, Parcelable {

    private String number;
    private ArrayList<ArrayList<Pair<Float,Float>>> points;

    public Cornice(String n, ArrayList<ArrayList<Pair<Float,Float>>> p) {
        number = n;
        if (p!=null) points = new ArrayList<>(p);
        else points=new ArrayList<>();
    }

    protected Cornice(Parcel in) {
        number = in.readString();
        points = in.readList(points);
    }

    public String getNumber () {
        return number;
    }

    public ArrayList<ArrayList<Pair<Float,Float>>> getPoints () {
        return points;
    }

    public static final Creator<Cornice> CREATOR = new Creator<Cornice>() {
        @Override
        public Cornice createFromParcel(Parcel in) {
            return new Cornice(in);
        }

        @Override
        public Cornice[] newArray(int size) {
            return new Cornice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        //MISSING
    }
}

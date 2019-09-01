package com.example.williamstest;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Cornice implements Serializable, Parcelable {

    private String numero;

    private ArrayList<ArrayList<Float>> points;

    public Cornice (String n, ArrayList<ArrayList<Float>> p) {
        numero = n;
        points = p;
    }

    protected Cornice(Parcel in) {
        numero = in.readString();
        points = in.readArrayList(ArrayList.class.getClassLoader());
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

    public String getNumero () { return numero; }

    public ArrayList<ArrayList<Float>> getPoints() { return points; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(numero);
        dest.writeList(points);
    }
}

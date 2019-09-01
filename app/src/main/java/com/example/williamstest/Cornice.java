package com.example.williamstest;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Cornice implements Serializable, Parcelable {

    private String numero;

    private ArrayList<ArrayList<Float>> points;

    private boolean completed;

    public Cornice (String n, ArrayList<ArrayList<Float>> p, boolean c) {
        numero = n;
        points = p;
        completed = c;
    }

    protected Cornice(Parcel in) {
        numero = in.readString();
        points = in.readArrayList(ArrayList.class.getClassLoader());
        completed = in.readByte() != 0;
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

    public boolean getCompleted () { return completed; }

    public void setCompleted (boolean c) { completed = c; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(numero);
        dest.writeList(points);
        dest.writeByte((byte) (completed ? 1 : 0));
    }
}

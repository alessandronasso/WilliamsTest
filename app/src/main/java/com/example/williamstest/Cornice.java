package com.example.williamstest;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Cornice implements Serializable, Parcelable {

    private String numero;

    private ArrayList<ArrayList<Float>> points;

    private boolean completed;

    private String titolo;

    public Cornice (String n, ArrayList<ArrayList<Float>> p, boolean c, String t) {
        numero = n;
        points = p;
        completed = c;
        titolo = t;
    }

    protected Cornice(Parcel in) {
        numero = in.readString();
        points = in.readArrayList(ArrayList.class.getClassLoader());
        completed = in.readByte() != 0;
        titolo = in.readString();
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

    public void setTitolo (String t) { titolo = t; }

    public String getTitolo () { return titolo; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(numero);
        dest.writeList(points);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(titolo);
    }
}

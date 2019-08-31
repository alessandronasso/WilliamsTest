package com.example.williamstest;

import java.io.Serializable;

public class TimesAndErasures implements Serializable {


    public TimesAndErasures(String c, int t1, int t2, int e, int u) {
        cornice = c;
        timeToDraw = t1;
        timeToComplete = t2;
        eraseN = e;
        undoN = u;
    }

    public String getCornice() {
        return cornice;
    }

    public void setCornice(String cornice) {
        this.cornice = cornice;
    }

    private String cornice;

    public int getTimeToDraw() {
        return timeToDraw;
    }

    public void setTimeToDraw(int timeToDraw) {
        this.timeToDraw = timeToDraw;
    }

    private int timeToDraw;

    public int getTimeToComplete() {
        return timeToComplete;
    }

    public void setTimeToComplete(int timeToComplete) {
        this.timeToComplete = timeToComplete;
    }

    private int timeToComplete;

    public int getEraseN() {
        return eraseN;
    }

    public void setEraseN(int eraseN) {
        this.eraseN = eraseN;
    }

    private int eraseN;

    public int getUndoN() {
        return undoN;
    }

    public void setUndoN(int undoN) {
        this.undoN = undoN;
    }

    private int undoN;
}

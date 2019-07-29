package com.example.williamstest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DrawingView extends View {

    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = Color.BLACK;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    //erase button
    private boolean erase=false;
    //structure for saving the single segment
    private ArrayList<Pair<Float,Float>> points = new ArrayList<>();
    //structure for saving all the lines drawn
    private ArrayList<ArrayList<Pair<Float,Float>>> segments = new ArrayList<>();
    //dimension of the square
    int height, width, diameter, offset;
    //check if user has drawn
    private boolean started = false;
    //timer
    private long startTime=0, startActivity;
    //string with times
    private String s1, s2;
    //string to define the current draw
    private String protocol, draw;
    //string to determinate the score in the draw in/out part
    private int scoreDrawOut=0, scoreDrawIn=0;
    //range of coordinates of the starting shape
    private ArrayList<Pair<Float,Float>> figura;
    //string to determinate the score of the symmetries
    private int symmetryInside=0, symmetryOutside=0, asymmetryInside=0, asymmetryOutside=0;
    //group of lines symmetric (to not check twice)
    private ArrayList<ArrayList<Pair<Float,Float>>> sym = new ArrayList<>();;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        startActivity = System.currentTimeMillis();
        setupDrawing();
    }

    public void setCornice(String p1, String c1) {
        protocol = p1;
        draw = c1;
    }

    public void setDimension (DisplayMetrics displaymetrics) {
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        diameter = width;
        if (height < width){
            diameter = height;
        }
        offset = (int) (0.32*diameter);
        diameter -= offset;
        String imageName = protocol+draw;
        setBackgroundResource(getResources().getIdentifier(imageName, "drawable", "com.example.williamstest"));
    }

    //setup drawing
    private void setupDrawing(){

        //prepare for drawing and setup paint stroke properties
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);

        /*canvas.drawRect(width/2 - diameter/2 ,
                (70),
                width/2 + diameter/2,
                1100, drawPaint);*/
    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!started) {
            started = true;
            long millis = System.currentTimeMillis() - startTime;
            s1 = String.format("%d sec", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toSeconds(startActivity));
            startTime = 0;
        }
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /*System.out.println(" "+touchX);
                System.out.println(" "+touchY);*/
                drawPath.moveTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                break;
            case MotionEvent.ACTION_MOVE:
                /*System.out.println(" "+touchX);
                System.out.println(" "+touchY);*/
                drawPath.lineTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                break;
            case MotionEvent.ACTION_UP:
                /*System.out.println(" "+touchX);
                System.out.println(" "+touchY);*/
                drawPath.lineTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                drawCanvas.drawPath(drawPath, drawPaint);
                if (!erase) {
                    ArrayList<Pair<Float, Float>> clone = new ArrayList<>(points);
                    segments.add(clone);
                } else removeErasedPoints(points);
                points.clear();
                drawPath.reset();
                //System.out.println("--------------------------");
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;
    }

    public void removeErasedPoints (ArrayList<Pair<Float, Float>> toRemove) {
        boolean found = false;
        for (int i=0; i<segments.size(); i++) {
            ArrayList<Pair<Float, Float>> linea = segments.get(i);
            for (int j=0; j<toRemove.size(); j++, found=false) {
                float x1 = toRemove.get(j).first;
                float y1 = toRemove.get(j).second;
                for (int x=0; !found && x<linea.size(); x++) {
                    if (linea.get(x).first<x1+3 && linea.get(x).first>x1-3 &&
                            linea.get(x).second<y1+3 && linea.get(x).second>y1-3) {
                        found = true;
                        linea.remove(x);
                    }
                }
            }
            if (linea.size()==0) segments.remove(i--);
        }
    }


    public void setErase(boolean isErase){
        erase=isErase;
        if(erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void restoreDraw () {
        if (segments.size()!=0) {
            drawPath = null;
            canvasBitmap = Bitmap.createBitmap(width, 350, Bitmap.Config.ARGB_8888);
            drawPath = new Path();
            invalidate();
            onSizeChanged(width, height, width, height);
            segments.remove(segments.size() - 1);
            for (int i = 0; i < segments.size(); i++) drawFromArrayList(segments.get(i));
        }
    }

    public void setShape (ArrayList<Pair<Float,Float>> points) {
        figura = new ArrayList<>(points);
    }

    public void drawFromArrayList(ArrayList<Pair<Float,Float>> points) {
        int pointCount = points.size();
        if (pointCount < 2) {
            return;
        }
        for (int i=0;i<pointCount;i++) {
            float touchX = points.get(i).first, touchY = points.get(i).second;
            if(i==0) {
                drawPath.moveTo(touchX, touchY);
            }
            drawPath.lineTo(touchX, touchY);
            if(i==pointCount-1) {
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
            }
        }
    }

    public void checkDrawOut() {
        boolean compreso = false;
        for (int x=0; x<segments.size(); x++) {
            ArrayList<Pair<Float,Float>> punti = segments.get(x);
            for (int j=0; j<punti.size(); j++, compreso=false) {
                for (int i=0; i<figura.size()/2-1 && !compreso; i++) {
                    float x1 = figura.get(i).first;
                    float x2 = figura.get(i+figura.size()/2).first;
                    float y1 = figura.get(i).second;
                    float y2 = figura.get(i+figura.size()/2).second;
                    float tmp;
                    if (x1<x2) { tmp=x1; x1=x2; x2=tmp; }
                    if (y1<y2) { tmp=y1; y1=y2; y2=tmp; }
                    if (punti.get(j).first < x1 && punti.get(j).first > x2 &&
                            punti.get(j).second < y1 && punti.get(j).second > y2)
                        compreso = true;
                }
                if (!compreso) scoreDrawOut = 1; else scoreDrawIn = 1;
            }
        }
    }

    public int getScoreDrawInOut () {
        checkDrawOut();
        if (scoreDrawOut == 0 && scoreDrawIn == 0) return 0;
        else if (scoreDrawOut == 1 && scoreDrawIn == 0) return 1;
        else if (scoreDrawOut == 0 && scoreDrawIn == 1) return 2;
        else return 3;
    }

    public String getReactionTime () {
        return s1;
    }

    public String getTimeToDraw () {
        long millis = System.currentTimeMillis() - startTime;
        s2 = String.format("%d sec", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toSeconds(startActivity));
        startTime = 0;
        return s2;
    }

    public void checkSymmetries() {
        boolean symmetryFound = false;
        if (segments.size() == 1) {
            if (isInside(segments.get(0))==1) asymmetryInside = 1;
            else if (isInside(segments.get(0))==2) asymmetryOutside = 1;
            else { asymmetryInside = 1; asymmetryOutside = 1; }
        } else {
            for (int z=0; z<segments.size()-1; z++, symmetryFound=false) {
                for (int i=z+1; i<segments.size(); i++) {
                    if (segments.get(z).size()>((segments.get(i).size())/1.5) || segments.get(i).size()>((segments.get(z).size())/1.5)) {
                        ArrayList<Pair<Float,Float>> copia = segments.get(z);
                        int nGroupsFirstShape = (segments.get(z).size()*10)/100;
                        int nValuesFirstShape[] = new int[10];
                        for (int j=0, j2=0; j<10; j++, j2+=nGroupsFirstShape) {
                            int sumValues=0;
                            sumValues+=copia.get(j2).first-copia.get(j2+nGroupsFirstShape-1).first;
                            sumValues+=copia.get(j2).second-copia.get(j2+nGroupsFirstShape-1).second;
                            nValuesFirstShape[j] = sumValues;
                        }
                        ArrayList<Pair<Float,Float>> copia2 = segments.get(i);
                        int nGroupSecondShape = (segments.get(i).size()*10)/100;
                        int nValuesSecondShape[] = new int[10];
                        for (int j=0, j2=0; j<10; j++, j2+=nGroupSecondShape) {
                            int sumValues=0;
                            sumValues+=copia2.get(j2).first-copia2.get(j2+nGroupSecondShape-1).first;
                            sumValues+=copia2.get(j2).second-copia2.get(j2+nGroupSecondShape-1).second;
                            nValuesSecondShape[j] = sumValues;
                        }
                        int differences[] = new int[10];
                        int numberOf = 0;
                        for (int x=0; x<10; x++) {
                            differences[x] = nValuesFirstShape[x] - nValuesSecondShape[x];
                            if (differences[x]<0) differences[x] = -differences[x];
                            if (differences[x]<nGroupsFirstShape*2.5) numberOf++;
                        }
                        if (numberOf>6) {
                            symmetryFound = true;
                            System.out.println("Figura1 "+z+"; Figura2: "+(i)+" SONO SIMMETRICI");
                            //check first shape
                            if (isInside(segments.get(z))==1) symmetryInside = 1;
                            else if (isInside(segments.get(z))==2) symmetryInside = 1;
                            else { symmetryInside = 1; symmetryOutside = 1; }
                            //check second shape
                            if (isInside(segments.get(i))==1) symmetryInside = 1;
                            else if (isInside(segments.get(i))==2) symmetryInside = 1;
                            else { symmetryInside = 1; symmetryOutside = 1; }
                            sym.add(segments.get(z));
                            sym.add(segments.get(i));
                        }
                    }
                }
                if (!symmetryFound && notSym(segments.get(z))) {
                    System.out.println("ENTRO");
                    if (isInside(segments.get(z))==1) asymmetryInside = 1;
                    else if (isInside(segments.get(z))==2) asymmetryOutside = 1;
                    else { asymmetryInside = 1; asymmetryOutside = 1; }
                }
            }
        }
    }

    public int isInside(ArrayList<Pair<Float,Float>> toCheck) {
        boolean compreso = false;
        int drawIn = 0, drawOut = 0;
        for (int j=0; j<toCheck.size(); j++, compreso=false) {
            for (int i=0; i<figura.size()/2-1 && !compreso; i++) {
                float x1 = figura.get(i).first;
                float x2 = figura.get(i+figura.size()/2).first;
                float y1 = figura.get(i).second;
                float y2 = figura.get(i+figura.size()/2).second;
                float tmp;
                if (x1<x2) { tmp=x1; x1=x2; x2=tmp; }
                if (y1<y2) { tmp=y1; y1=y2; y2=tmp; }
                if (toCheck.get(j).first < x1 && toCheck.get(j).first > x2 &&
                        toCheck.get(j).second < y1 && toCheck.get(j).second > y2)
                    compreso = true;
            }
            if (!compreso) drawOut = 1; else drawIn = 1;
        }
        if (drawIn == 1) return 1;
        else if (drawOut == 1) return 2;
        else return 3;
    }

    public int getSymmetryScore () {
        checkSymmetries();
        if (asymmetryOutside == 1 && asymmetryInside == 1) return 3;
        else if (asymmetryOutside == 1) return 1;
        else if (asymmetryInside == 1) return 2;
        else return 0;
    }

    public boolean notSym (ArrayList<Pair<Float,Float>> toCheck) {
        if (sym.size()==0) return true;
        else {
            for (int i=0; i<sym.size(); i++)
                if (sym.get(i).equals(toCheck)) return false;
        }
        return true;
    }

    public void clearBitmap () {
        canvasBitmap.recycle();
    }
}


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
import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DrawingView extends View {

    /**
     * Encapsulates compound geometric paths.
     */
    private Path drawPath;

    /**
     * They hold the style and color information about how to draw geometries, text and bitmaps.
     */
    private Paint drawPaint, canvasPaint;

    /**
     * The initial color of the brush
     */
    private int paintColor = Color.BLACK;

    /**
     * The area where the users can draw
     */
    private Canvas drawCanvas;

    /**
     * The drawing are converted in Bitmap
     */
    private Bitmap canvasBitmap;

    /**
     * Variable to check if the user has selected the rubbish.
     */
    private boolean erase=false;

    /**
     * Structure for saving the single segment drawn.
     */
    private ArrayList<Pair<Float,Float>> points = new ArrayList<>();

    /**
     * Structure for saving all the lines drawn.
     */
    private ArrayList<ArrayList<Pair<Float,Float>>> segments = new ArrayList<>();

    /**
     * Dimensions of the drawing area.
     */
    int height, width, diameter, offset;

    /**
     * Variable used to check if the user has started drawing.
     */
    private boolean started = false;

    /**
     * Timers to get the drawing times.
     */
    private long startTime=0, startActivity;

    /**
     * Strings with times and title of the draw.
     */
    private String s1, s2, title="";

    /**
     * Number of erasure done by the user.
     */
    private int eraseNumber = 0;

    /**
     * Number of undo done by the user.
     */
    private int undoNumber = 0;

    /**
     * Strings to define the current draw.
     */
    private String protocol, draw;

    /**
     * Strings to determinate the score in the draw in/out part.
     */
    private int scoreDrawOut=0, scoreDrawIn=0;

    /**
     * Range of the coordinates of the starting shape.
     */
    private ArrayList<Pair<Float,Float>> figura;

    /**
     * Strings to determinate the score of the symmetries.
     */
    private int asymmetryInside=0, asymmetryOutside=0;

    /**
     *  Group of lines symmetric (to not check twice).
     */
    private ArrayList<ArrayList<Pair<Float,Float>>> sym = new ArrayList<>();;

    /**
     * Center of the current shape.
     */
    private Pair<Float,Float> centerPoint;

    /**
     * Time spent by the user on the tutorial.
     */
    private long tutorialTime = 0;

    /**
     * Color of each line drawn by the user.
     */
    private ArrayList<Integer> lineColor = new ArrayList<>();


    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDrawingCacheEnabled(true);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        startActivity = System.currentTimeMillis();
        setupDrawing();
    }

    /**
     * This method set the current protocol and the current shape got
     * from the PaintingActivity.
     *
     * @param p1 the current protocol
     * @param c1 the current shape
     */
    public void setCornice(String p1, String c1) {
        protocol = p1;
        draw = c1;
    }

    /**
     * This method sets the display sizes and the background.
     *
     * @param displaymetrics the current display dimensions
     */
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


    /**
     * This method is used to prepare the user to draw.
     */
    private void setupDrawing(){
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        for (int i=0; i<segments.size(); i++) {
            if (lineColor.size()!=0) setPaintColor(lineColor.get(i));
            drawFromArrayList(segments.get(i));
        }
        setPaintColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!started) {
            started = true;
            long millis = System.currentTimeMillis() - startTime;
            s1 = String.format(Locale.ITALIAN, "%d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toSeconds(startActivity) - TimeUnit.MILLISECONDS.toSeconds(tutorialTime));
            startTime = 0;
        }
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                drawCanvas.drawPath(drawPath, drawPaint);
                if (!erase) {
                    lineColor.add(getPaintColor());
                    ArrayList<Pair<Float, Float>> clone = new ArrayList<>(points);
                    segments.add(clone);
                } else  {
                    eraseNumber++;
                    removeErasedPoints(points);
                }
                points.clear();
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    /**
     * This methods delete the points erased by the user from all
     * the segments previous drawn.
     *
     * @param toRemove the list of points erased by the user
     */
    public void removeErasedPoints (ArrayList<Pair<Float, Float>> toRemove) {
        for (int i=0; i<segments.size(); i++) {
            int initialSize = segments.get(i).size();
            ArrayList<Pair<Float, Float>> linea = segments.get(i);
            for (int j=0; j<toRemove.size(); j++) {
                float x1 = toRemove.get(j).first;
                float y1 = toRemove.get(j).second;
                for (int x=0; x<linea.size(); x++) {
                    if (linea.get(x).first<x1+10 && linea.get(x).first>x1-10 &&
                            linea.get(x).second<y1+10 && linea.get(x).second>y1-10) {
                        segments.get(i).remove(x);
                    }
                }
            }
            if (segments.get(i).size()==0 || ((segments.get(i).size()*100)/initialSize)<=20) {
                segments.remove(i--);
                lineColor.remove(i--);
            }
        }
    }

    /**
     * This methods is used to return the number of time the user erased
     * at least one point.
     *
     * @return how many times the user has erased.
     */
    public int getEraseNumber () {
        return eraseNumber;
    }

    /**
     * This method changes the size of the brush/eraser.
     *
     * @param size is the dimensione of the brush/eraser
     */
    public void updateStroke (int size) {
        drawPaint.setStrokeWidth(size);
    }


    /**
     * This method starts to clear the draw if the user is in
     * "erase" modality.
     *
     * @param isErase check if the user has pressed on the relative button
     */
    public void setErase(boolean isErase){
        erase=isErase;
        if(erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    /**
     * This method get called when the user press on the "undo" button. It cleans the paths
     * and restores all the segments drawn by the user before, except the last one.
     */
    public void restoreDraw () {
        if (segments.size()!=0) {
            segments.remove(segments.size() - 1);
            drawPath = null;
            canvasBitmap = Bitmap.createBitmap(width, 350, Bitmap.Config.ARGB_8888);
            drawPath = new Path();
            invalidate();
            onSizeChanged(width, height, width, height);
            undoNumber++;
        }
    }

    /**
     * This method loads the list of points of the current shape, so they can be used to check
     * if the draw has been done or there are symmetries out/in there.
     *
     * @param points the list of points of the current shape
     */
    public void setShape (ArrayList<Pair<Float,Float>> points) {
        figura = new ArrayList<>(points);
    }

    /**
     * This methods draw a segment by a list of given points.
     *
     * @param points segments to draw
     */
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

    /**
     * This method invert the points on the axes given in input.
     *
     * @param points the list of points to invert
     * @param ax the axes where invert the points
     * @return the list of points inverted
     */
    private ArrayList<Pair<Float,Float>> invertAxes (ArrayList<Pair<Float,Float>> points, String ax)  {
        float x_m = (points.get(0).first+points.get(points.size()-1).first)/2;
        float y_m = (points.get(0).second+points.get(points.size()-1).second)/2;
        for (int i=0; i<points.size()/2; i++) {
            if (ax.equals("x")) {
                float new_x = points.get(i).first - x_m;
                points.set(i, new Pair<Float, Float>(x_m-new_x,points.get(i).second));
            } else {
                float new_y = points.get(i).second - y_m;
                points.set(i, new Pair<Float, Float>(points.get(i).first, y_m-new_y));
            }
        }
        for (int i=points.size()/2; i<points.size(); i++) {
            if (ax.equals("x")) {
                float new_x = x_m - points.get(i).first;
                points.set(i, new Pair<Float, Float>(x_m+new_x, points.get(i).second));
            } else {
                float new_y = y_m - points.get(i).second;
                points.set(i, new Pair<Float, Float>(points.get(i).first, y_m+new_y));
            }
        }
        return points;
    }


    /**
     * Method used to check if the user has drawn outside the frame.
     */
    private void checkDrawOut() {
        Polygon.Builder shape = Polygon.Builder();
        for (int i=0; i<figura.size(); i++)
            shape.addVertex(new Point(figura.get(i).first, figura.get(i).second));
        Polygon polygon = shape.build();
        for (int x=0; x<segments.size(); x++) {
            ArrayList<Pair<Float, Float>> punti = segments.get(x);
            for (int j = 0; j < punti.size(); j++) {
                if (polygon.contains(new Point(punti.get(j).first, punti.get(j).second))) {
                    scoreDrawIn = 1;
                } else scoreDrawOut = 1;
            }
        }
    }


    /**
     * This method return the score of the "flessibilita'" part.
     *
     * @return the score following the Williams test guideline
     */
    public int getScoreDrawInOut () {
        checkDrawOut();
        if (scoreDrawOut == 0 && scoreDrawIn == 0) return 0;
        else if (scoreDrawOut == 1 && scoreDrawIn == 0) return 1;
        else if (scoreDrawOut == 0 && scoreDrawIn == 1) return 2;
        else return 3;
    }

    /**
     * This method returns the reaction time of the user. If he decide to
     * not draw, it will returns 0.
     *
     * @return a string corresponding the reaction time of the user.
     */
    public String getReactionTime () {
        if (s1==null) return "0"; else return s1;
    }

    /**
     * This method returns the time the user take to complete the draw.
     *
     * @return a string corresponding the completition time of the user.
     */
    public String getTimeToDraw () {
        long millis = System.currentTimeMillis() - startTime;
        s2 = String.format("%d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toSeconds(startActivity) - TimeUnit.MILLISECONDS.toSeconds(tutorialTime));
        startTime = 0;
        return s2;
    }

    /**
     * This method checks is there symmetries inside/outside the current shape.
     * The first thing it does is to check if there is only one element drawnby the child.
     * If yes
     */
    public void checkSymmetries() {
        boolean symmetryFound = false;
        if (segments.size() == 1) {
            if (isInside(segments.get(0))==1) asymmetryInside = 1;
            else if (isInside(segments.get(0))==2) asymmetryOutside = 1;
            else { asymmetryInside = 1; asymmetryOutside = 1; }
        } else {
            for (int z=0; z<segments.size() && segments.get(z).size()>9; z++, symmetryFound=false) {
                if (notSym(segments.get(z))) {
                    for (int i = z + 1; i < segments.size() && segments.get(i).size() > 9 && !symmetryFound; i++) {
                        if ((between(z, i) || between(i, z)) && notSym(segments.get(i)) && findAreas(segments.get(z), segments.get(i))) {
                            boolean similar = checkShape(z, segments.get(i));
                            boolean invertX = checkShape(z, invertAxes(segments.get(i), "x"));
                            boolean invertY = checkShape(z, invertAxes(segments.get(i), "y"));
                            if ((similar || invertX || invertY) && isInside(segments.get(z)) == isInside(segments.get(i))) {
                                symmetryFound = true;
                                sym.add(segments.get(z));
                                sym.add(segments.get(i));
                            }
                        }
                    }
                    if (!symmetryFound) {
                        if (isInside(segments.get(z)) == 1) asymmetryInside = 1;
                        else if (isInside(segments.get(z)) == 2) asymmetryOutside = 1;
                        else {
                            asymmetryInside = 1;
                            asymmetryOutside = 1;
                        }
                    }
                }
            }
        }
    }

    /**
     * This methods check the areas where the segments are.
     *
     * @param points First list of points to check
     * @param points2 Second list of points to check
     * @return true if the shapes are in the same areas
     */
    public boolean findAreas (ArrayList<Pair<Float,Float>> points, ArrayList<Pair<Float,Float>> points2) {
        boolean[] quadranti = {false, false, false, false};
        for (int i=0; i<points.size(); i++) {
            if (points.get(i).first<centerPoint.first && points.get(i).second>centerPoint.second) quadranti[0] = true;
        }
        for (int i=0; i<points.size(); i++) {
            if (points.get(i).first>centerPoint.first && points.get(i).second>centerPoint.second) quadranti[1] = true;
        }
        for (int i=0; i<points.size(); i++) {
            if (points.get(i).first>centerPoint.first && points.get(i).second<centerPoint.second) quadranti[2] = true;
        }
        for (int i=0; i<points.size(); i++) {
            if (points.get(i).first<centerPoint.first && points.get(i).second<centerPoint.second) quadranti[3] = true;
        }

        boolean[] quadranti2 = {false, false, false, false};
        for (int i=0; i<points2.size(); i++) {
            if (points2.get(i).first<centerPoint.first && points2.get(i).second>centerPoint.second) quadranti2[0] = true;
        }
        for (int i=0; i<points2.size(); i++) {
            if (points2.get(i).first>centerPoint.first && points2.get(i).second>centerPoint.second) quadranti2[1] = true;
        }
        for (int i=0; i<points2.size(); i++) {
            if (points2.get(i).first>centerPoint.first && points2.get(i).second<centerPoint.second) quadranti2[2] = true;
        }
        for (int i=0; i<points2.size(); i++) {
            if (points2.get(i).first<centerPoint.first && points2.get(i).second<centerPoint.second) quadranti2[3] = true;
        }

        int count = 0;
        for (int i=0; i<4; i++) {
            if (quadranti[i]!=quadranti2[i]) count++;
        }

        if (count>=1) return true; else return false;
    }

    /**
     * This method divides the shapes we want to compare in 10 parts each.
     * and checks how they change and the difference between them in each of the 10
     * parts, to see how they are similar. The operator are used to check if they are
     * symmetric. They can be symmetric through the X-axis or through the Y-axis.
     *
     * @param z the first segment to check
     * @return how two shapes are similar/symmetric
     */
    private boolean checkShape (int z, ArrayList<Pair<Float,Float>> points) {
        ArrayList<Pair<Float,Float>> copia = segments.get(z);
        int nGroupsFirstShape = (segments.get(z).size()*10)/100;
        int nValuesFirstShape[] = new int[10];
        for (int j=0, j2=0; j<10; j++, j2+=nGroupsFirstShape) {
            int sumValues=0;
            sumValues+=copia.get(j2).first-copia.get(j2+nGroupsFirstShape-1).first;
            sumValues+=copia.get(j2).second-copia.get(j2+nGroupsFirstShape-1).second;
            nValuesFirstShape[j] = sumValues;
        }
        ArrayList<Pair<Float,Float>> copia2 = points;
        int nGroupSecondShape = (copia2.size()*10)/100;
        int nValuesSecondShape[] = new int[10];
        for (int j=0, j2=0; j<10; j++, j2+=nGroupSecondShape) {
            int sumValues=0;
            sumValues+=copia2.get(j2).first-copia2.get(j2+nGroupSecondShape-1).first;
            sumValues+=copia2.get(j2).second-copia2.get(j2+nGroupSecondShape-1).second;
            nValuesSecondShape[j] = sumValues;
        }
        int differences[] = new int[10];
        int numberOf = 0;
        for (int index=0; index<10; index++) {
            differences[index] = nValuesFirstShape[index] - nValuesSecondShape[index];
            if (differences[index]<0) differences[index] = -differences[index];
            if (differences[index]<nGroupsFirstShape*2.5) numberOf++;
        }
        if (numberOf>=6) return true; else return false;
    }

    /**
     * This methods checks if the shapes have a similar size.
     *
     * @param i1 index of the first ArrayList
     * @param i2 index of the second ArrayList
     * @return if the shapes size are similar
     */
    private boolean between (int i1, int i2) {
        double flexValue = 2.0;
        return segments.get(i1).size()>(segments.get(i2).size()/flexValue) && segments.get(i1).size()<((segments.get(i2).size())*flexValue);
    }

    /**
     * This method checks if a segment has been drawn inside/outside (or both)
     * the shape.
     *
     * @param toCheck the segment to check
     * @return the position of the draw
     */
    public int isInside(ArrayList<Pair<Float,Float>> toCheck) {
        int drawIn = 0, drawOut = 0;
        Polygon.Builder shape = Polygon.Builder();
        for (int i=0; i<figura.size(); i++)
            shape.addVertex(new Point(figura.get(i).first, figura.get(i).second));
        Polygon polygon = shape.build();
        for (int x=0; x<toCheck.size(); x++) {
            if (polygon.contains(new Point(toCheck.get(x).first, toCheck.get(x).second)))
                drawIn = 1; else drawOut = 1;
        }
        if (drawIn == 1 && drawOut == 1) return 3;
        else if (drawOut == 1) return 2;
        else return 1;
    }

    /**
     * This method return the score of the "originalita'" part.
     *
     * @return the score following the Williams test guideline
     */
    public int getSymmetryScore () {
        checkSymmetries();
        if (asymmetryOutside == 1 && asymmetryInside == 1) return 3;
        else if (asymmetryOutside == 1) return 1;
        else if (asymmetryInside == 1) return 2;
        else return 0;
    }

    /**
     * This method checks if a segment has already found a symmetry.
     *
     * @param toCheck segment to check.
     * @return true/false depending if the segment
     * has already found a symmetry
     */
    public boolean notSym (ArrayList<Pair<Float,Float>> toCheck) {
        if (sym.size()==0) return true;
        else {
            for (int i=0; i<sym.size(); i++)
                if (sym.get(i).equals(toCheck)) return false;
        }
        return true;
    }


    /**
     * This method returns the whole segments drawn by the user.
     *
     * @return the ArrayList of segments
     */
    public ArrayList<ArrayList<Pair<Float,Float>>> getPoints () {
        return segments;
    }

    /**
     * This method sets the list of segments. It is used to restore
     * all the previous lines drawn by the user before skipping
     * to another shape.
     *
     * @param pts Points previously drawn by the user
     */
    public void setPoints (ArrayList<ArrayList<Pair<Float,Float>>> pts) {
        segments = new ArrayList<>(pts);
    }

    /**
     * Method used to set the center point of the current frame
     *
     * @param p1 central point of the shape
     */
    public void setCenterPoint (Pair p1) { centerPoint = p1; }

    /**
     * This method helps to clear the stack.
     */
    public void clearBitmap () {
        canvasBitmap.recycle();
    }

    /**
     * This method return the width of the drawing area which is used
     * to adapt it to every device.
     *
     * @return the width of the Canvas
     */
    public float getCanvasWidth () {
        return drawCanvas.getWidth();
    }

    /**
     * This method return the height of the drawing area which is used
     * to adapt it to every device.
     *
     * @return the height of the Canvas
     */
    public float getCanvasHeight () {
        return drawCanvas.getHeight();
    }

    /**
     * This method return the current brush color.
     *
     * @return the current brush color
     */
    public int getPaintColor () {
        return paintColor;
    }

    /**
     * This method returns the number of undo done by the user.
     *
     * @return how many undo have been done
     */
    public int getUndoNumber () {
        return undoNumber;
    }

    /**
     * This method changes the current drawing color.
     *
     * @param color the new color
     */
    public void setPaintColor (int color) {
        paintColor = color;
        drawPaint.setColor(paintColor);
    }

    /**
     * Method used to caluclate the time spent by the user on the tutorial.
     *
     * @param time Time spent by the user on the tutorial.
     */
    public void setTutorialTime (long time) {
        tutorialTime = time;
    }

    /**
     * Method used to get all the colors used in the draw.
     *
     * @return the colors
     */
    public ArrayList<Integer> getLineColor () {
        return lineColor;
    }

    /**
     * Method used to set all the colors used in the draw.
     *
     * @param lc list of color
     */
    public void setLineColor (ArrayList<Integer> lc) {
        lineColor = lc;
    }
}
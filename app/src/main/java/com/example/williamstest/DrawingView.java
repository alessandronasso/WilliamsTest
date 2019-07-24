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
    //points for saving the images
    private ArrayList<Pair<Float,Float>> points = new ArrayList<>();
    //dimension of the square
    int height, width, diameter, offset;
    //check if user has drawn
    private boolean started = false;
    //timer
    private long startTime=0, timeToDraw = 0, startActivity;
    //string with times
    private String s1, s2;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.uno);
        setupDrawing();
        setLayerType(LAYER_TYPE_HARDWARE, null);
        startActivity = System.currentTimeMillis();
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

        canvas.drawRect(width/2 - diameter/2 ,
                (70),
                width/2 + diameter/2,
                1100, drawPaint);
    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!started) {
            started = true;
            long millis = System.currentTimeMillis() - startTime;
            s1 = String.format("%d sec", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MILLISECONDS.toSeconds(startActivity));
            startTime = 0;
            Toast.makeText(getContext(),s1,
                    Toast.LENGTH_SHORT).show();
        }
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //System.out.print(" "+touchX+", "+touchY);
                drawPath.moveTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                break;
            case MotionEvent.ACTION_MOVE:
                //System.out.print(" "+touchX+", "+touchY);
                drawPath.lineTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                break;
            case MotionEvent.ACTION_UP:
                //System.out.print(" "+touchX+", "+touchY);
                drawPath.lineTo(touchX, touchY);
                points.add(new Pair<Float, Float>(touchX, touchY));
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                //System.out.println("--------");
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;
    }


    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    public void drawFromArrayList() {
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

    public void checkDrawOut(ArrayList<Pair<Float,Float>> figura) {
        for (int i=0; i<10; i++) { //Aggiugno && score!=0
            float x1 = figura.get(i).first;
            float x2 = figura.get(i+15).first;
            float y1 = figura.get(i).second;
            float y2 = figura.get(i+15).second;
            float tmp;
            if (x1<x2) { tmp=x1; x1=x2; x2=tmp; }
            if (y1<y2) { tmp=y1; y1=y2; y2=tmp; }
            for (int j=0; j<points.size(); j++) {
                if (points.get(j).first < x1 && points.get(j).first > x2 &&
                        points.get(j).second < y1 && points.get(j).second > y2) {
                    //Aggiungo lo score
                }
            }
        }
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
}


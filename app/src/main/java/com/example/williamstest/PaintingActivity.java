package com.example.williamstest;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class PaintingActivity extends AppCompatActivity implements OnClickListener {

    //custom drawing view
    private DrawingView drawView;
    //title of the draw
    private EditText title;
    //confirm button
    private Button b1;
    //draw button
    private ImageButton eraseBtn, drawBtn, undoBtn;
    //default shape points
    private ArrayList<Pair<Float, Float>> points = new ArrayList<>();
    //check the number of the next draw and the protocol
    private int nextDraw;
    private String protocol, cornice;
    //check the number of the app_draw folder
    private int folder=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        protocol = extras.getString("protocollo");
        cornice = extras.getString("cornice");

        setContentView(R.layout.activity_painting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //get drawing view
        drawView = (DrawingView)findViewById(R.id.drawing);
        drawView.setCornice(protocol, cornice);
        nextDraw = Integer.parseInt(cornice);

        //get the textview
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.toprow);
        LinearLayout paintLayout2 = (LinearLayout)findViewById(R.id.bottomrow);
        //textfield
        title = (EditText) paintLayout.findViewById(R.id.edittitle);
        //confirm button
        b1 = (Button) paintLayout2.findViewById(R.id.button_1);
        b1.setOnClickListener(this);
        //erase button
        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        //draw button
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        //undo button
        undoBtn = (ImageButton)findViewById(R.id.undo_btn);
        undoBtn.setOnClickListener(this);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        drawView.setDimension(displaymetrics);
        if (cornice.equals("1")) findFolder();
        else folder = extras.getInt("cartella");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.draw_btn) { drawView.updateStroke(5); drawView.setErase(false); }
        else if (view.getId() == R.id.erase_btn)  { drawView.setErase(true); drawView.updateStroke(25); }
        else if (view.getId() == R.id.undo_btn) drawView.restoreDraw();
        else if (view.equals(b1)){
            loadShapePoints();
            String flessibilita = "---";
            String originalita = drawView.getScoreDrawInOut()+"pt.";
            String fluidita = (drawView.getScoreDrawInOut()!=0) ? "1pt." : "0pt.";
            String elaborazione = drawView.getSymmetryScore()+"pt.";
            String titoli = drawView.getTitle();
            String tempoReazione = drawView.getReactionTime()+ " s";
            String tempoCompletamentoDisegno = drawView.getTimeToDraw()+ " s";
            String numeroCancellature = drawView.getEraseNumber()+"";
            saveImage();
            writeScore(fluidita, flessibilita, originalita, elaborazione, titoli, tempoReazione, tempoCompletamentoDisegno, numeroCancellature);
            System.out.println("Controllo simmetrie: "+drawView.getSymmetryScore()+"pt.");
            if (nextDraw != 1) {
                drawView.clearBitmap();
                Intent myIntent = new Intent(PaintingActivity.this, PaintingActivity.class);
                myIntent.putExtra("protocollo", protocol);
                myIntent.putExtra("cornice", Integer.toString(++nextDraw));
                myIntent.putExtra("cartella", folder);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PaintingActivity.this.startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(PaintingActivity.this, Result.class);
                myIntent.putExtra("protocollo", protocol);
                myIntent.putExtra("cartella", Integer.toString(folder));
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PaintingActivity.this.startActivity(myIntent);
            }
        }
    }

    /**
     * This method loads the coordinates of the current shape. They are stored
     * in the asset folder in a file whose name is based on the protocol and
     * the shape number.
     */
    public void loadShapePoints () {
        BufferedReader reader = null;
        float height = drawView.getCanvasHeight();
        float width = drawView.getCanvasWidth();
        float tmp = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(protocol+cornice)));
            String mLine;
            for (int elem=0; (mLine = reader.readLine()) != null; elem++) {
                mLine = mLine.replaceAll("\\s+","");
                if (elem%2==0) {
                    tmp = Float.parseFloat(mLine);
                } else {
                    float x = (width/2540)*(Float.valueOf(tmp));
                    float y = (height/1109)*(Float.valueOf(Float.parseFloat(mLine)));
                    Pair p1 = new Pair(x, y);
                    points.add(p1);
                }
            }
        } catch (IOException e) { } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) { }
            }
        }
        drawView.setShape(points);
    }

    /**
     * This method sets the folder of the current user.
     */
    public void findFolder () {
        int i = 1;
        File ex = new File("/data/user/0/com.example.williamstest/app_draw"+i);
        while (ex.isDirectory())
            ex = new File("/data/user/0/com.example.williamstest/app_draw"+(++i));
        folder = i;
    }

    /**
     * This method saves the .png image generated by the user in a folder
     * called "app_drawX".
     */
    public void saveImage () {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("draw"+(folder), Context.MODE_PRIVATE);
        try {
            File file = new File(directory.getAbsolutePath(), protocol + cornice + ".png");
            OutputStream fOut = new FileOutputStream(file);
            Bitmap pictureBitmap = drawView.getDrawingCache();
            pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method write the scores got by the user in a txt file inside
     * the app_drawX folder.
     *
     * @param f1 the points got in "fluidita'"
     * @param f2 the points got in "flessibilita'"
     * @param o the points got in "originalita'"
     * @param el the points got in "elaborazione"
     * @param tit The title given by the user
     * @param t1 The reaction time of the user since the image is open
     * @param t2 The completion time since the user starts drawing
     * @param n How many times the users use the erase button
     */
    public void writeScore (String f1, String f2, String o, String el, String tit, String t1, String t2, String n) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("draw"+(folder), Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath()+"/"+protocol + cornice+"_score.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(f1+"\n");
            outputStreamWriter.write(f2+"\n");
            outputStreamWriter.write(o+"\n");
            outputStreamWriter.write(el+"\n");
            outputStreamWriter.write(tit+"\n");
            outputStreamWriter.write(t1+"\n");
            outputStreamWriter.write(t2+"\n");
            outputStreamWriter.write(n+"\n");
            outputStreamWriter.write(protocol+"\n");
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() { }
}
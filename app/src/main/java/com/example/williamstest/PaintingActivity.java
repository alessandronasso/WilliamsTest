package com.example.williamstest;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.draw_btn) drawView.setErase(false);
        else if (view.getId() == R.id.erase_btn) drawView.setErase(true);
        else if (view.getId() == R.id.undo_btn) drawView.restoreDraw();
        else if (view.equals(b1)){
            loadShapePoints();
            String flessibilita = "---";
            String originalita = drawView.getScoreDrawInOut()+"pt.";
            String fluidita = (drawView.getScoreDrawInOut()!=0) ? "1pt." : "0pt.";
            String elaborazione = drawView.getSymmetryScore()+"pt.";
            String titoli = "---";
            String tempoReazione = drawView.getReactionTime()+ " secondi";
            String tempoCompletamentoDisegno = drawView.getTimeToDraw()+ " secondi";
            String numeroCancellature = drawView.getEraseNumber()+" cancellature";
            saveImage();
            writeScore(fluidita, flessibilita, originalita, elaborazione, titoli, tempoReazione, tempoCompletamentoDisegno, numeroCancellature);
            System.out.println("Controllo simmetrie: "+drawView.getSymmetryScore()+"pt.");
            if (nextDraw != 12) {
                drawView.clearBitmap();
                Intent myIntent = new Intent(PaintingActivity.this, PaintingActivity.class);
                myIntent.putExtra("protocollo", protocol);
                myIntent.putExtra("cornice", Integer.toString(++nextDraw));
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PaintingActivity.this.startActivity(myIntent);
            } else {
                Intent myIntent = new Intent(PaintingActivity.this, Result.class);
                myIntent.putExtra("protocollo", protocol);
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
        float tmp = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(protocol+cornice)));
            String mLine;
            for (int elem=0; (mLine = reader.readLine()) != null; elem++) {
                mLine = mLine.replaceAll("\\s+","");
                if (elem%2==0) {
                    tmp = Float.parseFloat(mLine);
                } else {
                    Pair p1 = new Pair(tmp, Float.parseFloat(mLine));
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
     * This method saves the .png image generated by the user in a folder
     * called "app_draw".
     */
    public void saveImage () {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("draw", Context.MODE_PRIVATE);
        try {
            OutputStream fOut = null;
            Integer counter = 0;
            File file = new File(directory.getAbsolutePath(), protocol + cornice + ".png");
            fOut = new FileOutputStream(file);

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
     * the app_draw folder.
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
        File directory = cw.getDir("draw", Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath()+"/"+protocol + cornice+"_score.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write("Fluidita': "+f1+"\n");
            outputStreamWriter.write("Flessibilita': "+f2+"\n");
            outputStreamWriter.write("Originalita': "+ o+"\n");
            outputStreamWriter.write("Elaborazione: "+el+"\n");
            outputStreamWriter.write("Titolo: "+tit+"\n");
            outputStreamWriter.write("Tempo di reazione: "+t1+"\n");
            outputStreamWriter.write("Tempo di completamento: "+t2+"\n");
            outputStreamWriter.write("Numero di cancellature: "+n+"\n");
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() { }
}
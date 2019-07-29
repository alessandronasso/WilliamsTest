package com.example.williamstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;
import android.provider.MediaStore;
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
import android.widget.Toast;


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
            /*drawView.setDrawingCacheEnabled(true);
            MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(), UUID.randomUUID().toString()+".png", "drawing");
            drawView.destroyDrawingCache();*/
            loadShapePoints();
            System.out.println("Controllo disegno: "+drawView.getScoreDrawInOut()+"pt.");
            System.out.println("Controllo simmetrie: "+drawView.getSymmetryScore()+"pt.");
            //drawView.checkSymmetries();
            if (nextDraw != 12) {
                drawView.clearBitmap();
                Intent myIntent = new Intent(PaintingActivity.this, PaintingActivity.class);
                myIntent.putExtra("protocollo", protocol);
                myIntent.putExtra("cornice", Integer.toString(++nextDraw));
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PaintingActivity.this.startActivity(myIntent);
            } else {
                //.........
            }
        }
    }

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
}
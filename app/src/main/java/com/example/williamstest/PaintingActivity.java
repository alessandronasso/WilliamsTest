package com.example.williamstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Timer;
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


public class PaintingActivity extends AppCompatActivity implements OnClickListener {

    //custom drawing view
    private DrawingView drawView;
    //title of the draw
    private EditText title;
    //confirm button
    private Button b1;
    //draw button
    private ImageButton eraseBtn, drawBtn;
    //default shape points
    private ArrayList<Pair<Float, Float>> points = new ArrayList<>();
    //reaction time and draw complete
    private Timer firstInput, timeToDraw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //get drawing view
        drawView = (DrawingView)findViewById(R.id.drawing);

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
        else if (view.equals(b1)){
            /*drawView.setDrawingCacheEnabled(true);
            MediaStore.Images.Media.insertImage(getContentResolver(), drawView.getDrawingCache(), UUID.randomUUID().toString()+".png", "drawing");
            drawView.destroyDrawingCache();*/
            points.add(new Pair<>((float)964.44904, (float)833.00244));
            points.add(new Pair<>((float)1000.2999, (float)824.9536));
            points.add(new Pair<>((float)1021.32153, (float)817.01733));
            points.add(new Pair<>((float)1046.9531, (float)796.4092));
            points.add(new Pair<>((float)1104.582, (float)746.33655));
            points.add(new Pair<>((float)1234.375, (float)616.4894));
            points.add(new Pair<>((float)1272.0715, (float)582.92206));
            points.add(new Pair<>((float)1313.951, (float)543.9166));
            points.add(new Pair<>((float)1372.4609, (float)500.00684));
            points.add(new Pair<>((float)1404.6266, (float)484.02173));
            points.add(new Pair<>((float)1435.0825, (float)474.02417));
            points.add(new Pair<>((float)1475.9781, (float)465.01538));
            points.add(new Pair<>((float)1497.2399, (float)465.01538));
            points.add(new Pair<>((float)1572.8906, (float)493.03052));

            points.add(new Pair<>((float)963.90625, (float)964.0144));
            points.add(new Pair<>((float)1008.1243, (float)931.98926));
            points.add(new Pair<>((float)1038.0078, (float)915.8871));
            points.add(new Pair<>((float)1068.8873, (float)901.5469));
            points.add(new Pair<>((float)1118.0728, (float)886.81824));
            points.add(new Pair<>((float)1238.3048, (float)869.2201));
            points.add(new Pair<>((float)1308.0026, (float)854.9751));
            points.add(new Pair<>((float)1390.09, (float)833.00244));
            points.add(new Pair<>((float)1445.6525, (float)807.0198));
            points.add(new Pair<>((float)1540.9375, (float)701.6879));
            points.add(new Pair<>((float)1565.9375, (float)612.6849));
            points.add(new Pair<>((float)1568.9062,(float) 591.4955));
            points.add(new Pair<>((float)1568.9062, (float)578.4767));
            points.add(new Pair<>((float)1199.0243,(float) 877.0027));
            points.add(new Pair<>((float)1568.9062, (float)564.0022));
            drawView.checkDrawOut(points);
        }
    }
}
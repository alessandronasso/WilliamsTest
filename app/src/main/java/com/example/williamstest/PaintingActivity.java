package com.example.williamstest;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import petrov.kristiyan.colorpicker.ColorPicker;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class PaintingActivity extends AppCompatActivity implements OnClickListener {

    /**
     * The area where the user draws.
     */
    private DrawingView drawView;

    /**
     * The area where the user writes the title of the draw
     */
    private EditText title;

    /**
     * The confirm buttons.
     */
    private Button b2, b3;

    /**
     * The images representing the brush, the eraser and the undo.
     */
    private ImageButton eraseBtn, drawBtn, undoBtn, colorPickerBtn;

    /**
     * List of points of the current shape.
     */
    private ArrayList<Pair<Float, Float>> points = new ArrayList<>();

    /**
     * The current protocol/shape number.
     */
    private String protocol, cornice;

    /**
     * Number of the current folder.
     */
    private int folder = 1;

    /**
     * String to define if the color palette has been enabled.
     */
    private String palette;

    /**
     * The user who has logged in.
     */
    private String logged = "";

    /**
     * Total erasure/undo in the user's draw.
     */
    private int totalErase = 0, totalUndo = 0;

    /**
     * Standard name for each draw.
     */
    private String titoli = "Senza nome";

    /**
     * List of draws to complete.
     */
    private ArrayList<Cornice> corniciNC = null;

    /**
     * List of times of the draws skipped and completed.
     */
    private ArrayList<TimesAndErasures> tempi = null;

    /**
     * Time spent by the user on the tutorial.
     */
    private long timeTutorial = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        protocol = extras.getString("protocollo");
        cornice = extras.getString("cornice");
        palette = extras.getString("palette");
        logged = extras.getString("userLogged");
        corniciNC = (ArrayList<Cornice>)getIntent().getSerializableExtra(("corniciNC"));
        if (corniciNC==null) corniciNC = new ArrayList<>();
        tempi = (ArrayList<TimesAndErasures>)getIntent().getSerializableExtra(("tempi"));
        if (tempi==null) tempi = new ArrayList<>();
        setContentView(R.layout.activity_painting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        drawView = (DrawingView) findViewById(R.id.drawing);
        drawView.setCornice(protocol, cornice);
        if (findLocation()!=-1) drawView.setLineColor(corniciNC.get(findLocation()).getColors());
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.toprow);
        LinearLayout paintLayout2 = (LinearLayout) findViewById(R.id.bottomrow);
        title = (EditText) paintLayout.findViewById(R.id.edittitle);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titoli = s.toString();
            }
        });
        b2 = (Button) paintLayout2.findViewById(R.id.bb_2);
        b2.setOnClickListener(this);
        b3 = (Button) paintLayout2.findViewById(R.id.bb_3);
        b3.setOnClickListener(this);
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);
        drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);
        undoBtn = (ImageButton) findViewById(R.id.undo_btn);
        undoBtn.setOnClickListener(this);
        if (palette.equals("yes")) {
            colorPickerBtn = (ImageButton) findViewById(R.id.colorPicker);
            colorPickerBtn.setOnClickListener(this);
            colorPickerBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });
        } else {
            colorPickerBtn = (ImageButton) findViewById(R.id.colorPicker);
            colorPickerBtn.setImageResource(R.drawable.palette_disabled);
            colorPickerBtn.setOnClickListener(this);
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        drawView.setDimension(displaymetrics);
        if (extras.getString("first").equals("yes")) {
            timeTutorial = System.currentTimeMillis();
            showTutorial();
            findFolder();
            writeInfoTest(extras.getString("gender"), extras.getString("eta"), extras.getString("userLogged"));
        } else folder = extras.getInt("cartella");
        if (findLocation()!=-1) title.setText(corniciNC.get(findLocation()).getTitolo());
        restorePoints();
    }

    /**
     * Method which is used to invoke the tutorial on the drawing view.
     */
    private void showTutorial () {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(400);
        final int[] numElemMostrati = {0};

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        sequence.setConfig(config);
        sequence.addSequenceItem(drawBtn, "Questo è lo strumento matita!\nClicca qui sopra per cominciare" +
                "a disegnare!", "HO CAPITO");
        sequence.addSequenceItem(eraseBtn, "Questo è lo strumento gomma!\nClicca qui sopra per cancellare",
                "HO CAPITO");
        sequence.addSequenceItem(undoBtn, " Questo è lo strumento undo!\n Clicca qui sopra per cancellare\n istantaneamente" +
                        "l'ultima\n linea disegnata!", "HO CAPITO");
        if (palette.equals("yes")) sequence.addSequenceItem(colorPickerBtn, "Questo è lo strumento per i colori!\nClicca qui sopra per cambiare il colore\ndella matita!", "HO CAPITO");
        sequence.addSequenceItem(b2, "Questo è il tasto Avanti!\nUtilizza questo tasto per passare al prossimo disegno!", "HO CAPITO");
        sequence.addSequenceItem(b3, "Questo è il tasto Indietro!\nUtilizza questo tasto per tornare al disegno precedente!", "HO CAPITO");
        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
            @Override
            public void onDismiss(MaterialShowcaseView materialShowcaseView, int i) {
                numElemMostrati[0]++;
                if (numElemMostrati[0]==2)
                    drawView.setTutorialTime(System.currentTimeMillis() - timeTutorial);
            }
        });
        sequence.start();
    }

    /**
     * Method from the ColorPicker package used to open
     * a custom dialog box.
     *
     */
    private void openDialog() {
        drawView.setErase(false);
        ColorPicker colorPicker = new ColorPicker(this);
        colorPicker.setRoundColorButton(true);
        colorPicker.setColors(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.GRAY, Color.MAGENTA,
                Color.DKGRAY, Color.YELLOW, Color.LTGRAY);
        colorPicker.show();
        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
            @Override
            public void onChooseColor(int position,int color) {
                drawView.setPaintColor(color);
                drawView.updateStroke(5);
                drawView.setErase(false);
            }

            @Override
            public void onCancel(){
                //DO NOTHING
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.draw_btn) {
            drawView.updateStroke(5);
            drawView.setErase(false);
        } else if (view.getId() == R.id.erase_btn) {
            drawView.setErase(true);
            drawView.updateStroke(25);
        } else if (view.getId() == R.id.undo_btn) drawView.restoreDraw();
        else if (view.equals(b3)) {  //TASTO INDIETRO
            tempi.add(new TimesAndErasures(cornice, Integer.parseInt(drawView.getReactionTime()), Integer.parseInt(drawView.getTimeToDraw()), drawView.getEraseNumber(), drawView.getUndoNumber()));
            if (findLocation() == -1) {
                corniciNC.add(new Cornice(cornice, savePoints(), false, title.getText().toString(), getColorSelected()));
            } else
                corniciNC.set(findLocation(), (new Cornice(cornice, savePoints(), false, title.getText().toString(), getColorSelected())));
            if (Integer.parseInt(cornice)-1 != 0) {
                loadShapePoints();
                loadCenterPoint();
                String flessibilita = "---";
                String originalita = drawView.getScoreDrawInOut() + "pt.";
                String fluidita = (drawView.getScoreDrawInOut() != 0) ? "1pt." : "0pt.";
                String elaborazione = drawView.getSymmetryScore() + "pt.";
                String tempoReazione = (fluidita.equals("1pt.")) ? "" + getTimeToDrawTime() + " s" : "0" + " s";
                String tempoCompletamentoDisegno = (fluidita.equals("1pt.")) ? "" + getTotalCompletitionTime() + " s" : "0" + " s";
                String numeroCancellature = totalErase + "";
                String undo = totalUndo + "";
                saveImage();
                writeScore(fluidita, flessibilita, originalita, elaborazione, titoli, tempoReazione, tempoCompletamentoDisegno, numeroCancellature, undo);
                drawView.clearBitmap();
                Intent myIntent = new Intent(PaintingActivity.this, PaintingActivity.class);
                myIntent.putExtra("protocollo", protocol);
                myIntent.putExtra("cornice", Integer.toString(Integer.parseInt(cornice)-1));
                myIntent.putExtra("cartella", folder);
                myIntent.putExtra("first", "no");
                myIntent.putExtra("palette", palette);
                myIntent.putExtra("userLogged", logged);
                myIntent.putExtra("corniciNC", corniciNC);
                myIntent.putExtra("tempi", tempi);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PaintingActivity.this.startActivity(myIntent);
            } else
                Toast.makeText(this, "Non ci sono altri disegni", Toast.LENGTH_LONG).show();
        } else if (view.equals(b2)) { //TASTO AVANTI
            tempi.add(new TimesAndErasures(cornice, Integer.parseInt(drawView.getReactionTime()), Integer.parseInt(drawView.getTimeToDraw()), drawView.getEraseNumber(), drawView.getUndoNumber()));
            if (findLocation() == -1) {
                corniciNC.add(new Cornice(cornice, savePoints(), false, title.getText().toString(), getColorSelected()));
            } else
                corniciNC.set(findLocation(), (new Cornice(cornice, savePoints(), false, title.getText().toString(), getColorSelected())));
            loadShapePoints();
            loadCenterPoint();
            String flessibilita = "---";
            String originalita = drawView.getScoreDrawInOut() + "pt.";
            String fluidita = (drawView.getScoreDrawInOut() != 0) ? "1pt." : "0pt.";
            String elaborazione = drawView.getSymmetryScore() + "pt.";
            String tempoReazione = (fluidita.equals("1pt.")) ? "" + getTimeToDrawTime() + " s" : "0" + " s";
            String tempoCompletamentoDisegno = (fluidita.equals("1pt.")) ? "" + getTotalCompletitionTime() + " s" : "0" + " s";
            String numeroCancellature = totalErase + "";
            String undo = totalUndo + "";
            saveImage();
            writeScore(fluidita, flessibilita, originalita, elaborazione, titoli, tempoReazione, tempoCompletamentoDisegno, numeroCancellature, undo);
            if (Integer.parseInt(cornice)+1 ==13) {
                new androidx.appcompat.app.AlertDialog.Builder(PaintingActivity.this)
                        .setTitle("Chiusura test")
                        .setMessage("Vuoi veramente concludere il test?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent myIntent = new Intent(PaintingActivity.this, Result.class);
                                myIntent.putExtra("protocollo", protocol);
                                myIntent.putExtra("cartella", Integer.toString(folder));
                                myIntent.putExtra("userLogged", logged);
                                myIntent.putExtra("palette", palette);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PaintingActivity.this.startActivity(myIntent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            } else {
                drawView.clearBitmap();
                Intent myIntent = new Intent(PaintingActivity.this, PaintingActivity.class);
                myIntent.putExtra("protocollo", protocol);
                myIntent.putExtra("cornice", Integer.toString(Integer.parseInt(cornice)+1));
                myIntent.putExtra("cartella", folder);
                myIntent.putExtra("first", "no");
                myIntent.putExtra("palette", palette);
                myIntent.putExtra("userLogged", logged);
                myIntent.putExtra("corniciNC", corniciNC);
                myIntent.putExtra("tempi", tempi);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PaintingActivity.this.startActivity(myIntent);
            }
        }
    }

    /**
     * Method used to calculate the reaction time of the frame.
     *
     * @return the reaction time
     */
    public int getTimeToDrawTime () {
        int timeToDraw = 0;
        for (int i=0; i<tempi.size() && timeToDraw==0; i++) {
            if (tempi.get(i).getCornice().equals(cornice)) {
                timeToDraw = tempi.get(i).getTimeToDraw();
            }
        }
        return timeToDraw;
    }

    /**
     * Method used to calculate the completition time of the frame
     * according to all the draws made by the user.
     *
     * @return the completition time
     */
    public int getTotalCompletitionTime () {
        int firstIndexElem = -1;
        int sumTot = 0;
        for (int i=0; i<tempi.size() && firstIndexElem==-1; i++) {
            if (tempi.get(i).getCornice().equals(cornice)) firstIndexElem=i;
        }
        for (int i=firstIndexElem; i<tempi.size(); i++) {
            sumTot +=tempi.get(i).getTimeToComplete();
        }
        return sumTot;
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
     * This method loads the center points of the current shape. They are stored
     * in the asset folder in a file whose name is based on the protocol and
     * the shape number.
     */
    public void loadCenterPoint () {
        BufferedReader reader = null;
        float height = drawView.getCanvasHeight();
        float width = drawView.getCanvasWidth();
        float tmp = 0;
        Pair p1 = new Pair<Float, Float>(null, null);
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(protocol+cornice+"_center")));
            String mLine;
            for (int elem=0; (mLine = reader.readLine()) != null; elem++) {
                mLine = mLine.replaceAll("\\s+","");
                if (elem%2==0) {
                    tmp = Float.parseFloat(mLine);
                } else {
                    float x = (width/2540)*(Float.valueOf(tmp));
                    float y = (height/1109)*(Float.valueOf(Float.parseFloat(mLine)));
                    p1 = new Pair(x, y);
                }
            }
        } catch (IOException e) { } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) { }
            }
        }
        drawView.setCenterPoint(p1);
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
    public void writeScore (String f1, String f2, String o, String el, String tit, String t1, String t2, String n, String undo) {
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
            outputStreamWriter.write(undo+"\n");
            if (tit.equals("Senza nome")) outputStreamWriter.write("0pt.");
            else outputStreamWriter.write("1pt.");
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method writes some info about the user, such as the gender and the age.
     *
     * @param gender The gender of the user
     * @param eta The age of the user
     * @param userLogged The logged user.
     */
    public void writeInfoTest (String gender, String eta, String userLogged) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("draw"+(folder), Context.MODE_PRIVATE);
        File file = new File(directory.getAbsolutePath()+"/infotest.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(userLogged+"\n");
            outputStreamWriter.write(gender+"\n");
            outputStreamWriter.write(eta+"\n");
            outputStreamWriter.write(protocol+"\n");
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            outputStreamWriter.write(currentDate);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method searches if the draw is already in the list.
     *
     * @return the position of the current shape in the list
     */
    public int findLocation () {
        for (int i=0; i<corniciNC.size(); i++) {
            if (corniciNC.get(i).getNumero().equals(cornice)) return i;
        }
        return -1;
    }

    /**
     * This method saves the points drawn by the user if he decides to skip the current shape.
     */
    public ArrayList<ArrayList<Float>> savePoints () {
        ArrayList<ArrayList<Pair<Float,Float>>> points = new ArrayList<>(drawView.getPoints());
        ArrayList<ArrayList<Float>> toReturn = new ArrayList<>();
        if (points.size()!=0) {
            for (int i=0; i<points.size(); i++) {
                ArrayList<Float> tmp = new ArrayList<>(points.get(i).size());
                for (int j=0; j<points.get(i).size(); j++) {
                    tmp.add(points.get(i).get(j).first);
                    tmp.add(points.get(i).get(j).second);
                }
                toReturn.add(tmp);
            }
        }
        return toReturn;
    }

    /**
     * This method restores the points drawn before skipping the shape.
     *
     */
    public void restorePoints ()  {
        ArrayList<ArrayList<Pair<Float,Float>>> points = new ArrayList<>();
        ArrayList<Pair<Float,Float>> toSet = new ArrayList<>();
        for (int i=0; i<corniciNC.size(); i++) {
            if (corniciNC.get(i).getNumero().equals(cornice)) {
                for (int j=0; j<corniciNC.get(i).getPoints().size(); j++) {
                    toSet = new ArrayList<>();
                    for (int x=0; x<corniciNC.get(i).getPoints().get(j).size(); x++) {
                        toSet.add(new Pair<>(corniciNC.get(i).getPoints().get(j).get(x), corniciNC.get(i).getPoints().get(j).get(++x)));
                    }
                    points.add(toSet);
                }
            }
        }
        drawView.setPoints(points);
    }

    /**
     * Method used to get all the colors used in the current frame.
     *
     * @return the color used
     */
    public ArrayList<Integer> getColorSelected () {
        return drawView.getLineColor();
    }

    @Override
    public void onBackPressed() { }
}
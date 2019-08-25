package com.example.williamstest;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

public class SlideAdapter extends PagerAdapter {
    /**
     * Current context of the app.
     */
    Context context;

    /**
     * The Layout of the slides.
     */
    LayoutInflater inflater;

    /**
     * The current protocol/folder
     */
    private String protocollo, folder;

    /**
     * The editText where the user can modify the score.
     */
    private EditText f, t;

    /**
     * The new value modfied by the user.
     */
    private String newValue="", newValue2="";

    /**
     * The user who has logged in.
     */
    private String logged="";

    /**
     * The position of the current shape.
     */
    private int pos;


    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(RelativeLayout)object);
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);
        RelativeLayout layoutslide = (RelativeLayout) view.findViewById(R.id.slidelinearlayout);
        ImageView imgslide = getImage(view, position);
        TableLayout tl = (TableLayout) view.findViewById(R.id.tl);
        pos = position;
        try {
            setTableText(view, getDescription(position));
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }

    /**
     * Set the current protocol given by the Result activity.
     *
     * @param p current protocol
     */
    public void setProtocol (String p) { protocollo = p; }

    /**
     * This method sets the user that has logged in before.
     *
     * @param l the user logged
     */
    public void setLogged (String l) {logged = l; }

    /**
     * This methods is used to set the folder number where retrieve
     * the images and the text files.
     *
     * @param s folder number
     */
    public void setFolder (String s) { folder = s; }

    /**
     * Get the image drawn by the user from the app_draw folder.
     *
     * @param view current image
     * @param position current shape to check
     * @return the image drawn by the user
     */
    public ImageView getImage (View view, int position ) {
        ImageView img = (ImageView) view.findViewById(R.id.slideimg);
        Bitmap b;
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("draw", Context.MODE_PRIVATE);
            if (new File(directory.getAbsolutePath()+folder, protocollo+(position+1)+".png").exists()) {
                File f = new File(directory.getAbsolutePath() + folder, protocollo + (position + 1) + ".png");
                b = BitmapFactory.decodeStream(new FileInputStream(f));
            } else {
                int resID = this.context.getResources().getIdentifier(""+protocollo+""+(position + 1), "drawable", context.getPackageName());
                b = BitmapFactory.decodeResource(context.getResources(), resID);
            }
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * This method return a string corresponding the score of the user.
     *
     * @param position current shape to check
     * @return the score of the user
     * @throws IOException
     */
    public String getDescription (int position) throws IOException {
        String content = "";
        ContextWrapper cw = new ContextWrapper(context());
        File directory = cw.getDir("draw", Context.MODE_PRIVATE);
        LineNumberReader reader = new LineNumberReader(new FileReader(directory.getAbsolutePath()+folder+"/"+protocollo+(position+1)+"_score.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
           content+=line+"\n";
        }
        return content;
    }

    /**
     * This method create and initialize the tables with the
     * user scores.
     *
     * @param view view of the current activity
     * @param content the stats of the user draw
     */
    private void setTableText (View view, String content) {
        f = (EditText) view.findViewById(R.id.fl_item_2);
        TextView fl = (TextView) view.findViewById(R.id.f_item_1);
        TextView o = (TextView) view.findViewById(R.id.o_item_3);
        TextView el = (TextView) view.findViewById(R.id.el_item_4);
        t = (EditText) view.findViewById(R.id.t_item_5);
        TextView t1 = (TextView) view.findViewById(R.id.tempo_item_1);
        TextView t2 = (TextView) view.findViewById(R.id.tempo_item_2);
        TextView t3 = (TextView) view.findViewById(R.id.n_3);
        TextView t4 = (TextView) view.findViewById(R.id.n_4);
        TextView txttitle= (TextView) view.findViewById(R.id.txttitle);
        String[] values = content.split("\n");
        f.setText(values[1]);
        f.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newValue = s.toString();
                newValue = newValue.replaceAll("[^\\d]", "" );
                newValue = newValue+"pt.";
            }
        });
        t.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newValue2 = s.toString();
                newValue2 = newValue2.replaceAll("[^\\d]", "" );
                newValue2 = newValue2+"pt.";
            }
        });
        fl.setText(values[0]);
        o.setText(values[2]);
        el.setText(values[3]);
        t.setText(values[9]);
        t1.setText(values[5]);
        t2.setText(values[6]);
        t3.setText(values[7]);
        t4.setText(values[8]);
        txttitle.setText(values[4]);
    }

    /**
     * This method modify the score based on what the user has inserted in the table.
     *
     * @throws IOException
     */
    public void modifyFile () throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("draw", Context.MODE_PRIVATE);
        File inputFile = new File(directory.getAbsolutePath()+folder+"/"+protocollo+(pos)+"_score.txt");
        File tempFile = new File(directory.getAbsolutePath()+folder+"/"+protocollo+(pos)+"_scoretmp.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        for (int i=0; (currentLine = reader.readLine()) != null; i++) {
            if (i==1 && !newValue.equals("")) writer.write(newValue + System.getProperty("line.separator"));
            else if (i==9 && !newValue2.equals("")) writer.write(newValue2 + System.getProperty("line.separator"));
            else writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        tempFile.renameTo(inputFile);
    }

    /**
     * This method return the context of the application.
     *
     * @return the current context
     */
    private Context context() {
        return context;
    }
}

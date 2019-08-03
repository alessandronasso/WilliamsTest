package com.example.williamstest;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    private String protocollo;
    private String folder;


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
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("draw", Context.MODE_PRIVATE);
            File f = new File(directory.getAbsolutePath()+folder, protocollo+(position+1)+".png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
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
        TextView f = (TextView) view.findViewById(R.id.f_item_1);
        TextView fl = (TextView) view.findViewById(R.id.fl_item_2);
        TextView o = (TextView) view.findViewById(R.id.o_item_3);
        TextView el = (TextView) view.findViewById(R.id.el_item_4);
        TextView t = (TextView) view.findViewById(R.id.t_item_5);
        TextView t1 = (TextView) view.findViewById(R.id.tempo_item_1);
        TextView t2 = (TextView) view.findViewById(R.id.tempo_item_2);
        TextView t3 = (TextView) view.findViewById(R.id.n_3);
        TextView txttitle= (TextView) view.findViewById(R.id.txttitle);
        String[] values = content.split("\n");
        f.setText(values[0]);
        fl.setText(values[1]);
        o.setText(values[2]);
        el.setText(values[3]);
        if (values[4].equals("Senza nome")) t.setText("0pt.");
        else t.setText("0pt.");
        t1.setText(values[5]);
        t2.setText(values[6]);
        t3.setText(values[7]);
        txttitle.setText(values[4]);



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

package com.example.williamstest;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;
    private String protocollo;

    // list of titles
    private String[] lst_title;


    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        setListOfTitles();;
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);
        LinearLayout layoutslide = (LinearLayout) view.findViewById(R.id.slidelinearlayout);
        TextView txttitle= (TextView) view.findViewById(R.id.txttitle);
        TextView description = (TextView) view.findViewById(R.id.txtdescription);
        layoutslide.setBackgroundColor(Color.rgb(55,55,55));
        ImageView imgslide = getImage(view, position);
        txttitle.setText(lst_title[position]);
        try {
            description.setText(getDescription(position));
        } catch (IOException e) {
            e.printStackTrace();
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }

    public void setProtocol (String p) { protocollo = p; }

    public void setListOfTitles () {
        lst_title = new String[12];
        for (int i=0; i<12; i++) {
            lst_title[i] = "Immagine "+protocollo.toUpperCase()+(i+1);
        }
    }


    public ImageView getImage (View view, int position ) {
        ImageView img = (ImageView) view.findViewById(R.id.slideimg);
        try {
            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("draw", Context.MODE_PRIVATE);
            System.out.println(directory.getAbsolutePath());
            File f = new File(directory.getAbsolutePath(), protocollo+(position+1)+".png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return img;
    }

    public String getDescription (int position) throws IOException {
        String content = "";
        ContextWrapper cw = new ContextWrapper(context());
        File directory = cw.getDir("draw", Context.MODE_PRIVATE);
        System.out.println(directory.getAbsolutePath()+"/"+protocollo+(position+1)+"_score.txt");
        LineNumberReader reader = new LineNumberReader(new FileReader(directory.getAbsolutePath()+"/"+protocollo+(position+1)+"_score.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
           content+=line+"\n";
        }
        return content;
    }

    private Context context() {
        return context;
    }
}

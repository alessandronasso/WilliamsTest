package com.example.williamstest;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class FinalResult extends AppCompatActivity {

    private int[] sum = new int[4];
    private String protocol, folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle extras = getIntent().getExtras();
        protocol = extras.getString("protocollo");
        folder = extras.getString("cartella");
        for (int i=0; i<4; i++) sum[i]=0;
        try {
            loadContent(protocol, folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView f = (TextView) findViewById(R.id.f_item_1);
        TextView fl = (TextView) findViewById(R.id.fl_item_2);
        TextView o = (TextView) findViewById(R.id.o_item_3);
        TextView el = (TextView) findViewById(R.id.el_item_4);
        TextView t = (TextView) findViewById(R.id.t_item_5);
        f.setText(sum[0]+"pt.");
        fl.setText("---");
        o.setText(sum[1]+"pt.");
        el.setText(sum[2]+"pt.");
        t.setText(sum[3]+"pt.");
    }

    public void loadContent (String protocollo, String folder) throws IOException {
        String content = "";
        ContextWrapper cw = new ContextWrapper(FinalResult.this);
        File directory = cw.getDir("draw", Context.MODE_PRIVATE);
        for (int i=1; i<13; i++, content="") {
            LineNumberReader reader = new LineNumberReader(new FileReader(directory.getAbsolutePath() + folder + "/" + protocollo + (i) + "_score.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }
            String[] values = content.split("\n");
            sum[0] += Integer.parseInt(values[0].replace("pt.",""));
            sum[1] += Integer.parseInt(values[2].replace("pt.",""));
            sum[2] += Integer.parseInt(values[3].replace("pt.",""));
            if (values[4].equals("Senza nome")) sum[3] += 0;
            else sum[3] += 0;
        }
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(FinalResult.this, Result.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("protocollo", protocol);
        myIntent.putExtra("cartella", folder);
        FinalResult.this.startActivity(myIntent);
    }
}

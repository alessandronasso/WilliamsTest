package com.example.williamstest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;

public class Result extends AppCompatActivity {
    /**
     * The area with the results.
     */
    private ViewPager viewPager;

    /**
     * The slide mechanism of the results area.
     */
    private SlideAdapter myadapter;

    /**
     * The current protocol/folder and logged user.
     */
    private String logged, protocol, folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Button b1 = (Button) findViewById(R.id.bb_4);
        Button b2 = (Button) findViewById(R.id.applica);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Result.this, FinalResult.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.putExtra("protocollo", protocol);
                myIntent.putExtra("cartella", folder);
                myIntent.putExtra("userLogged", logged);
                Result.this.startActivity(myIntent);
            }
        });
        Bundle extras = getIntent().getExtras();
        protocol = extras.getString("protocollo");
        logged = extras.getString("userLogged");
        folder = extras.getString("cartella");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myadapter = new SlideAdapter(this);
        myadapter.setProtocol(protocol);
        myadapter.setFolder(folder);
        myadapter.setLogged(logged);
        viewPager.setAdapter(myadapter);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myadapter.modifyFile();
                    AlertDialog alertDialog = new AlertDialog.Builder(Result.this).create();
                    alertDialog.setTitle("Avviso");
                    alertDialog.setMessage("Le modifiche sono state eseguite con successo");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        Intent myIntent = new Intent(Result.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("userLogged", extras.getString("userLogged"));
        Result.this.startActivity(myIntent);
    }
}

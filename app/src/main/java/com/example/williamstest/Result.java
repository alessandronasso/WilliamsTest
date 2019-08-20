package com.example.williamstest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

public class Result extends AppCompatActivity {
    private ViewPager viewPager;
    private SlideAdapter myadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle extras = getIntent().getExtras();
        String protocol = extras.getString("protocollo");
        String folder = extras.getString("cartella");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myadapter = new SlideAdapter(this);
        myadapter.setProtocol(protocol);
        myadapter.setFolder(folder);
        viewPager.setAdapter(myadapter);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(Result.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Result.this.startActivity(myIntent);
    }
}

package com.example.williamstest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File dir = new File("/data/user/0/com.example.williamstest/");
        if (!dir.isDirectory()) throw new IllegalStateException();
        Bundle extras = getIntent().getExtras();
        userLogged = extras.getString("userLogged");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.form_elements, null, false);
        final CheckBox myCheckBox = (CheckBox) formElementsView
                .findViewById(R.id.myCheckBox);

        final RadioGroup genderRadioGroup = (RadioGroup) formElementsView
                .findViewById(R.id.genderRadioGroup);

        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);

        final Button button = findViewById(R.id.button_1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setView(formElementsView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                                RadioButton selectedRadioButton = (RadioButton) formElementsView.findViewById(selectedId);
                                Intent myIntent = new Intent(MainActivity.this, PaintingActivity.class);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                if (myCheckBox.isChecked()) myIntent.putExtra("palette", "yes");
                                else myIntent.putExtra("palette", "no");
                                myIntent.putExtra("gender", selectedRadioButton.getText());
                                String eta = nameEditText.getText().toString();
                                if (eta.length()!=0) myIntent.putExtra("eta", eta);
                                else myIntent.putExtra("eta", "0");
                                myIntent.putExtra("protocollo", "a");
                                myIntent.putExtra("cornice", "1" + "");
                                myIntent.putExtra("userLogged", userLogged);
                                myIntent.putExtra("first", "yes");
                                MainActivity.this.startActivity(myIntent);
                                dialog.cancel();
                            }

                        }).show().getWindow().setLayout(600, 600);
            }
        });
        final Button button2 = findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setView(formElementsView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                                RadioButton selectedRadioButton = (RadioButton) formElementsView.findViewById(selectedId);
                                Intent myIntent = new Intent(MainActivity.this, PaintingActivity.class);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                if (myCheckBox.isChecked()) myIntent.putExtra("palette", "yes");
                                else myIntent.putExtra("palette", "no");
                                myIntent.putExtra("gender", selectedRadioButton.getText());
                                String eta = nameEditText.getText().toString();
                                if (eta.length()!=0) myIntent.putExtra("eta", eta);
                                else myIntent.putExtra("eta", "0");
                                myIntent.putExtra("protocollo", "b");
                                myIntent.putExtra("cornice", "1" + "");
                                myIntent.putExtra("first", "yes");
                                myIntent.putExtra("userLogged", userLogged);
                                MainActivity.this.startActivity(myIntent);
                                dialog.cancel();
                            }

                        }).show();
            }
        });
        final Button button3 = findViewById(R.id.button_3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, UserList.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.putExtra("userLogged", userLogged);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }
}

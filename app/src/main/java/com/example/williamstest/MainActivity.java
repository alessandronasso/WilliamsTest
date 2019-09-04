package com.example.williamstest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /**
     * The string representing the user who logged in.
     */
    private String userLogged;

    /**
     * Protocol selected by the user.
     */
    private String protocolSelected = "A";

    /**
     * Button used to start drawing.
     */
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        button = findViewById(R.id.button_1);

        final Button b1 = (Button) formElementsView
                .findViewById(R.id.button_form);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) formElementsView.getParent();
                if (parent != null) parent.removeView(formElementsView);
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this).setView(formElementsView);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                b1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final int selectedId= genderRadioGroup.getCheckedRadioButtonId();
                        RadioButton selectedRadioButton = (RadioButton) formElementsView.findViewById(selectedId);
                        if (selectedId==-1)
                            loadProtocol(myCheckBox.isChecked(), nameEditText.getText().toString(), "/");
                        else
                            loadProtocol(myCheckBox.isChecked(), nameEditText.getText().toString(), selectedRadioButton.getText().toString());
                    }
                });
                alert.show().getWindow().setLayout(800,550);
            }
        });
        final TextView button3 = findViewById(R.id.button_3);
        button3.setShadowLayer(20, 0, 0, Color.BLACK);
    }

    /**
     * Used for debugging. This method deletes all the tests.
     */
    public void deleteAllTests () {
        File dir = new File("/data/user/0/com.example.williamstest/");
        if (!dir.isDirectory()) throw new IllegalStateException();
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("app_draw")) {
                File[] contents = file.listFiles();
                if (contents != null) {
                    for (File f : contents) {
                        f.delete();
                    }
                }
                file.delete();
            }
        }
    }

    /**
     * This method is used to switch between protocols.
     *
     * @param view the current view.
     */
    public void changeProtocol (View view) {
        if (protocolSelected.equals("A")) protocolSelected = "B";
        else protocolSelected = "A";
        button.setText("Protocollo "+protocolSelected);
    }

    /**
     * This method is used to load the list of tests.
     *
     * @param view the current view.
     */
    public void loadListOfTests (View view) {
        Intent myIntent = new Intent(MainActivity.this, UserList.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("userLogged", userLogged);
        MainActivity.this.startActivity(myIntent);
    }

    /**
     * This method is used to load one of the protocol chosen by the user.
     *
     * @param palette Used to check if the user has enabled the palette.
     * @param eta The eta inserted by the user.
     * @param gender The gender checked by the user.
     */
    public void loadProtocol (boolean palette, String eta, String gender) {
        Intent myIntent = new Intent(MainActivity.this, PaintingActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (palette) myIntent.putExtra("palette", "yes");
        else myIntent.putExtra("palette", "no");
        myIntent.putExtra("gender", gender);
        if (eta.length()!=0) myIntent.putExtra("eta", eta);
        else myIntent.putExtra("eta", "0");
        myIntent.putExtra("protocollo", protocolSelected.toLowerCase());
        myIntent.putExtra("cornice", "1" + "");
        myIntent.putExtra("userLogged", userLogged);
        myIntent.putExtra("first", "yes");
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Vuoi effettuare il logout?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MainActivity.this.startActivity(myIntent);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert=builder.create();
        alert.show();
    }
}

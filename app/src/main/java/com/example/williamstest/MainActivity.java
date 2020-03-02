package com.example.williamstest;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    /**
     * The string representing the user who logged in.
     */
    private String userLogged;

    /**
     * The string representing if the palette has been enabled.
     */
    private String paletteSelected;

    /**
     * Protocol selected by the user.
     */
    private String protocolSelected = "A";

    /**
     * Button used to start drawing.
     */
    private Button button;

    /**
     * The string representing when the user was born.
     */
    private String eta = "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        userLogged = extras.getString("userLogged");
        paletteSelected = extras.getString("palette");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.form_elements, null, false);

        final RadioGroup genderRadioGroup = (RadioGroup) formElementsView
                .findViewById(R.id.genderRadioGroup);

        final Button dateSelect = (Button) formElementsView
                .findViewById(R.id.dateSelect);

        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = 01;
                int mMonth = 01;
                int mDay = 2000;

                final Calendar c1 = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.MySpinnerDatePickerStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        c1.set(Calendar.DAY_OF_MONTH, day);
                        c1.set(Calendar.MONTH, month);
                        c1.set(Calendar.YEAR, year);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        eta = dateFormat.format(c1.getTime());
                    }
                }, mDay, mMonth, mYear);
                datePickerDialog.show();
            }

        });

        button = findViewById(R.id.button_1);

        final Button b1 = (Button) formElementsView
                .findViewById(R.id.button_form);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ViewGroup parent = (ViewGroup) formElementsView.getParent();
                if (parent != null) parent.removeView(formElementsView);
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this).setView(formElementsView);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                b1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        final int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                        RadioButton selectedRadioButton = (RadioButton) formElementsView.findViewById(selectedId);
                        if (selectedId == -1)
                            loadProtocol(eta, "/");
                        else
                            loadProtocol(eta, selectedRadioButton.getText().toString());
                    }
                });
                alert.show().getWindow().setLayout(800, 550);
            }
        });
        final TextView button3 = findViewById(R.id.button_3);
        button3.setShadowLayer(20, 0, 0, Color.BLACK);
    }

    /**
     * Used for debugging. This method deletes all the tests.
     */
    public void deleteAllTests() {
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
    public void changeProtocol(View view) {
        if (protocolSelected.equals("A")) protocolSelected = "B";
        else protocolSelected = "A";
        button.setText("Protocollo " + protocolSelected);
    }

    /**
     * This method is used to load the list of tests.
     *
     * @param view the current view.
     */
    public void loadListOfTests(View view) {
        Intent myIntent = new Intent(MainActivity.this, UserList.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("userLogged", userLogged);
        myIntent.putExtra("palette", paletteSelected);
        MainActivity.this.startActivity(myIntent);
    }

    /**
     * This method is used to load one of the protocol chosen by the user.
     *
     * @param eta    The eta inserted by the user.
     * @param gender The gender checked by the user.
     */
    public void loadProtocol(String eta, String gender) {
        Intent myIntent = new Intent(MainActivity.this, InstructionsActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("palette", paletteSelected);
        myIntent.putExtra("gender", gender);
        if (eta.length() != 0) myIntent.putExtra("eta", eta);
        else myIntent.putExtra("eta", "0");
        myIntent.putExtra("protocollo", protocolSelected.toLowerCase());
        myIntent.putExtra("cornice", "1" + "");
        myIntent.putExtra("userLogged", userLogged);
        myIntent.putExtra("first", "yes");
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

        AlertDialog alert = builder.create();
        alert.show();
    }
}

package com.example.williamstest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    /**
     * List of user who can access the app.
     */
    private ArrayList<String> acceptedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        acceptedUsers = new ArrayList<String>();
        acceptedUsers.add("0000"); //SUPERUSER
        acceptedUsers.add("3671"); acceptedUsers.add("9531");
        acceptedUsers.add("4289"); acceptedUsers.add("8371");
        acceptedUsers.add("5903"); acceptedUsers.add("1783");
        acceptedUsers.add("2468"); acceptedUsers.add("6128");
        acceptedUsers.add("7142"); acceptedUsers.add("1234");

        final EditText edT = findViewById(R.id.editTextCode);
        final Switch switchColor = (Switch) findViewById(R.id.switchColor);
        Button openAct = findViewById(R.id.cirLoginButton);
        openAct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (acceptedUsers.contains(edT.getText().toString()) && checkCompleted(edT.getText().toString())) {
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    myIntent.putExtra("userLogged", edT.getText().toString());
                    if (switchColor.isChecked()) myIntent.putExtra("palette", "yes");
                    else myIntent.putExtra("palette", "no");
                    LoginActivity.this.startActivity(myIntent);
                } else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Errore")
                            .setMessage("Codice non valido!")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        });
    }

    private boolean checkCompleted (String userSubmitted) {
        File inputFile = new File("/data/user/0/com.example.williamstest/testCompleted.txt");
        if (!inputFile.exists()) return true;
        else  {
            try {
                BufferedReader br = new BufferedReader(new FileReader(inputFile));
                String line;
                while ((line = br.readLine()) != null)
                    if (userSubmitted.equals(line)) return false;
                br.close();
            } catch (IOException e) { }
        }
        return true;
    }
}
package com.example.williamstest;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

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
        acceptedUsers.add("0000");
        acceptedUsers.add("1111");
        final EditText edT = findViewById(R.id.editTextCode);
        final Switch switchColor = (Switch) findViewById(R.id.switchColor);
        Button openAct = findViewById(R.id.cirLoginButton);
        openAct.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (acceptedUsers.contains(edT.getText().toString())) {
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
}
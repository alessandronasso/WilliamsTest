package com.example.williamstest;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

public class InstructionsActivity extends AppCompatActivity {

    /**
     * The current protocol/shape number.
     */
    private String protocol, cornice, eta, gender;

    /**
     * String to define if the color palette has been enabled.
     */
    private String palette;

    /**
     * The user who has logged in.
     */
    private String logged = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        Bundle extras = getIntent().getExtras();
        protocol = extras.getString("protocollo");
        cornice = extras.getString("cornice");
        palette = extras.getString("palette");
        logged = extras.getString("userLogged");
        gender = extras.getString("gender");
        eta = extras.getString("eta");
        System.out.println(protocol + " "+cornice+" "+gender+" "+cornice+" "+eta+" "+logged);
        TextView instructionsText = (TextView)findViewById(R.id.instruction);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(instructionsText, 1, 24, 4,
                TypedValue.COMPLEX_UNIT_DIP);
        instructionsText.setText("Nelle prossime pagine ci sono 12 cornici che contengono alcune linee o abbozzi\n" +
                "di forme. Partendo dalle linee o dalle forme che trovate potrete disegnare oggetti\n" +
                "e figure interessanti. Cercate di fare dei disegni in tutte e 12 le cornici, disegnando\n" +
                "cose originali a cui nessun altro penserebbe. Disegnate rapidamente con matite\n" +
                "a colori. Le cornici sono numerate, quindi seguite l'ordine stabilito dalla prima\n" +
                "cornice all'ultima. È importante! Sulla riga che si trova sotto ciascuna cornice\n" +
                "scrivete un nome o un titolo per ciascun disegno dicendo quel che rappresenta.\n" +
                "Cercate di scegliere un nome intelligente e interessante per ciascuno del vostri\n" +
                "disegni. Questo è un esercizio per vedere quanto siete creativi.");
        Button clickButton = (Button) findViewById(R.id.next_button);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(InstructionsActivity.this, PaintingActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.putExtra("palette", palette);
                myIntent.putExtra("gender", gender);
                myIntent.putExtra("eta", eta);
                myIntent.putExtra("protocollo", protocol.toLowerCase());
                myIntent.putExtra("cornice", cornice);
                myIntent.putExtra("userLogged", logged);
                myIntent.putExtra("first", "yes");
                InstructionsActivity.this.startActivity(myIntent);
            }
        });
    }
}

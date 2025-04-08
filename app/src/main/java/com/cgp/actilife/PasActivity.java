package com.cgp.actilife;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class PasActivity extends AppCompatActivity {

    private EditText inputQuantite_pas;
    private ProgressBar bar_pas;
    private TextView pct_bar_pas;
    private TextView text_bar_pas;
    private Button btnAjouterPlat;

    // Variable pour stocker les pas actuels (100 dans votre exemple)
    private int currentSteps = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pas);

        // Initialisation des vues
        inputQuantite_pas = findViewById(R.id.inputQuantite_pas);
        bar_pas = findViewById(R.id.bar_pas);
        pct_bar_pas = findViewById(R.id.pct_bar_pas); // Ajoutez un id à votre TextView pour le pourcentage
        text_bar_pas = findViewById(R.id.text_bar_pas); // Ajoutez un id à votre TextView "Vous avez fait X pas sur Y"
        btnAjouterPlat = findViewById(R.id.btn_para_pas);

        // Mettre à jour l'UI avec les valeurs initiales
        updateUI(currentSteps, 500); // 500 est la valeur initiale de l'objectif

        btnAjouterPlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantiteStr = inputQuantite_pas.getText().toString();
                if (!quantiteStr.isEmpty()) {
                    int newGoal = Integer.parseInt(quantiteStr);
                    updateUI(currentSteps, newGoal);
                } else {
                    Toast.makeText(PasActivity.this, "Veuillez entrer un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Gestion de la flèche de retour
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUI(int steps, int goal) {
        // Mettre à jour le texte des pas
        text_bar_pas.setText(String.format("Vous avez fait %d pas sur %d", steps, goal));

        // Calculer le pourcentage
        int percentage = (int) (((float) steps / goal) * 100);

        // Mettre à jour la barre de progression
        bar_pas.setMax(100);
        bar_pas.setProgress(percentage);

        // Mettre à jour le texte du pourcentage
        pct_bar_pas.setText(String.format("%d%%", percentage));
    }
}
package com.cgp.actilife;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class PasActivity extends AppCompatActivity {

    private EditText inputQuantite_pas;
    private DatabaseOpenHelper db;
    private ProgressBar bar_pas;
    private TextView pct_bar_pas;
    private TextView text_bar_pas;
    private Button btnAjouterPas;
    private TextView texte_motivation_pas;

    private int currentSteps = 100; // valeur simulée (pas capteur)
    private String dateAujourdhui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pas);

        db = new DatabaseOpenHelper(this);


        inputQuantite_pas = findViewById(R.id.inputQuantite_pas);
        bar_pas = findViewById(R.id.bar_pas);
        pct_bar_pas = findViewById(R.id.pct_bar_pas);
        text_bar_pas = findViewById(R.id.text_bar_pas);
        texte_motivation_pas = findViewById(R.id.texte_moitivation_pas);
        btnAjouterPas = findViewById(R.id.btn_para_pas);

        dateAujourdhui = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        int objectifDuJour = 500; // valeur par défaut si rien dans la base

        // 🔍 On vérifie s'il existe une ligne pour aujourd'hui
        List<Map<String, String>> enregistrements = db.getAll(ConstDB.PAS);
        for (Map<String, String> ligne : enregistrements) {
            if (dateAujourdhui.equals(ligne.get(ConstDB.PAS_DATE_DU_JOUR))) {
                try {
                    String objectifStr = ligne.get(ConstDB.PAS_OBJECTIF_PAS);
                    if (objectifStr != null && !objectifStr.isEmpty()) {
                        objectifDuJour = Integer.parseInt(objectifStr);
                    }
                } catch (Exception e) {
                    objectifDuJour = 500;
                }
                break;
            }
        }

        updateUI(currentSteps, objectifDuJour);

        btnAjouterPas.setOnClickListener(v -> {
            String quantiteStr = inputQuantite_pas.getText().toString();
            if (!quantiteStr.isEmpty()) {
                int newGoal = Integer.parseInt(quantiteStr);

                // Réinitialisation du nombre de pas à 0 lors de mise à jour de l'objectif
                currentSteps = 0;

                boolean dejaExistant = false;
                long idExistant = -1;

                // Vérifie si une ligne existe déjà pour aujourd’hui
                for (Map<String, String> ligne : enregistrements) {
                    if (dateAujourdhui.equals(ligne.get(ConstDB.PAS_DATE_DU_JOUR))) {
                        String idStr = ligne.get("id");
                        if (idStr != null && !idStr.isEmpty()) {
                            try {
                                idExistant = Long.parseLong(idStr);
                                dejaExistant = true;
                            } catch (NumberFormatException ignored) {}
                        }
                        break;
                    }
                }

                // Mise à jour ou insertion avec pas = 0
                Map<String, Object> fields = new HashMap<>();
                fields.put(ConstDB.PAS_DATE_DU_JOUR, dateAujourdhui);
                fields.put(ConstDB.PAS_OBJECTIF_PAS, newGoal);
                fields.put(ConstDB.PAS_NB_PAS_AUJOURDHUI, currentSteps); // 🔁 reset des pas

                if (dejaExistant && idExistant != -1) {
                    db.updateTableWithId(ConstDB.PAS, fields, (int) idExistant);
                } else {
                    db.insertData(ConstDB.PAS, fields);
                }

                updateUI(currentSteps, newGoal);
            } else {
                Toast.makeText(this, "Veuillez entrer un nombre", Toast.LENGTH_SHORT).show();
            }
        });


        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());
    }

    private void updateUI(int steps, int goal) {
        text_bar_pas.setText(String.format("Vous avez fait %d pas sur %d", steps, goal));
        int percentage = (int) (((float) steps / goal) * 100);
        bar_pas.setMax(100);
        bar_pas.setProgress(percentage);
        pct_bar_pas.setText(String.format("%d%%", percentage));

        // 💬 Motivation
        String motivation = db.getMotivation(ConstDB.MOTIVATIONS_TYPE_PAS);
        texte_motivation_pas.setText(motivation);
    }
}

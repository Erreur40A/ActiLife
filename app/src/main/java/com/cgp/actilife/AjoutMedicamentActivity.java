package com.cgp.actilife;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class AjoutMedicamentActivity extends AppCompatActivity {

    private EditText editTextNom;
    private EditText editTextType;
    private LinearLayout layoutHeuresAjoutees;

    private ArrayList<String> heures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_medicament);

        editTextNom = findViewById(R.id.nomMedicament);
        editTextType = findViewById(R.id.typeMedicament);
        EditText editTextHeure = findViewById(R.id.heureMedicament);
        Button btnAjouterMedicament2 = findViewById(R.id.btnAjouterMedicament2);
        layoutHeuresAjoutees = findViewById(R.id.layoutHeuresAjoutees);
        ImageView btnRetour = findViewById(R.id.btnRetour1);

        // Retour
        btnRetour.setOnClickListener(v -> finish());

        // Click sur champ heure → ouvre un TimePicker
        editTextHeure.setOnClickListener(v -> showTimePicker());

        // Ajouter médicament
        btnAjouterMedicament2.setOnClickListener(v -> {
            String nom = editTextNom.getText().toString();
            String type = editTextType.getText().toString();

            if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(type) || heures.isEmpty()) {
                Toast.makeText(this, "Remplissez tous les champs et ajoutez au moins une heure", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Medicament> resultList = new ArrayList<>();
            for (String heure : heures) {
                resultList.add(new Medicament(nom, type, heure));
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("liste_medocs", resultList);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void showTimePicker() {
        int hour = 12;
        int minute = 0;

        // TimePicker en mode spinner avec style Holo
        TimePickerDialog timePicker = new TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (view, selectedHour, selectedMinute) -> {
                    String heureFormattee = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    if (!heures.contains(heureFormattee)) {
                        heures.add(heureFormattee);
                        afficherHeureDansLayout(heureFormattee);
                    } else {
                        Toast.makeText(this, "Heure déjà ajoutée", Toast.LENGTH_SHORT).show();
                    }
                },
                hour,
                minute,
                true
        );

        // Fond transparent pour un look propre
        timePicker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePicker.show();
    }

    private void afficherHeureDansLayout(String heure) {
        TextView tv = new TextView(this);
        tv.setText("• " + heure);
        tv.setTextSize(18);
        layoutHeuresAjoutees.addView(tv);
    }
}

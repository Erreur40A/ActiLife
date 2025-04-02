package com.cgp.actilife; // Remplacez par le nom de votre package

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AjoutMedicamentActivity extends AppCompatActivity {
    private ImageView btnRetour;
    private EditText editTextNom, editTextType, editTextHeure;
    private Button btnAjouterMedicament2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_medicament); // Raccorde le layout XML

        // Trouver l'ImageView du bouton retour
        this.editTextNom = findViewById(R.id.nomMedicament);
        this.editTextType = findViewById(R.id.typeMedicament);
        this.editTextHeure = findViewById(R.id.heureMedicament);
        this.btnAjouterMedicament2 = findViewById(R.id.btnAjouterMedicament2);
        this.btnRetour = (ImageView) findViewById(R.id.btnRetour1) ;
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Créer une intention pour ouvrir la page RappelMedicamentActivity
                //Intent intent = new Intent(getApplicationContext(), RappelMedicamentActivity.class);
                //startActivity(intent); // Lance l'activité
                finish(); // Optionnel : pour fermer l'activité actuelle et ne pas revenir à celle-ci
            }
        });
        btnAjouterMedicament2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupération des données saisies dans les champs de texte
                String nom = editTextNom.getText().toString();
                String type = editTextType.getText().toString();
                String heure = editTextHeure.getText().toString();

                // Vérification que les champs sont remplis
                if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(type) || TextUtils.isEmpty(heure)) {
                    Toast.makeText(AjoutMedicamentActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                } else {
                    // Créer un objet Medicament avec les valeurs récupérées
                    Medicament nouveauMedicament = new Medicament(nom, type, heure);

                    // Créer un intent pour retourner les données à l'activité appelante
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("nom", nom);
                    resultIntent.putExtra("type", type);
                    resultIntent.putExtra("heure", heure);
                    setResult(RESULT_OK, resultIntent);
                    finish(); // Ferme cette activité et retourne à la précédente
                }
            }
        });


    }
}

package com.cgp.actilife;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SupprimmerMedicamentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentSuppressionAdapter adapter;
    private ArrayList<Medicament> listeMedocs;
    private int tailleAvantSuppression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppression_medoc);

        // Utilisation directe de la liste partagée depuis RappelMedicamentActivity
        listeMedocs = (ArrayList<Medicament>) RappelMedicamentActivity.medicamentList;

        // On garde une trace de la taille initiale pour détecter une suppression
        tailleAvantSuppression = listeMedocs.size();

        recyclerView = findViewById(R.id.recyclerViewMedicaments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicamentSuppressionAdapter(listeMedocs);
        recyclerView.setAdapter(adapter);

        // Bouton retour (depuis l'en-tête)
        AppCompatButton btnRetour = findViewById(R.id.btnRetourDeSuppression);
        if (btnRetour != null) {
            btnRetour.setOnClickListener(v -> {
                if (listeMedocs.size() < tailleAvantSuppression) {
                    setResult(RESULT_OK); // Il y a eu des suppressions
                }
                finish();
            });
        }
    }
}

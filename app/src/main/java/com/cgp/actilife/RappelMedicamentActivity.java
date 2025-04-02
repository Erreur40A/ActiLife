package com.cgp.actilife;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RappelMedicamentActivity extends AppCompatActivity {

    private TextView textViewMedicaments;
    private RecyclerView recyclerViewMedicaments;
    private MedicamentAdapter medicamentAdapter;

    // Liste rendue publique et statique pour être partagée avec la page de suppression
    public static List<Medicament> medicamentList = new ArrayList<>();

    private Button btnAjouterMedicament;
    private Button btnSupprimerMedicamen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rappel_medicament);

        recyclerViewMedicaments = findViewById(R.id.recyclerViewMedicaments);
        btnAjouterMedicament = findViewById(R.id.btnAjouterMedicament);
        btnSupprimerMedicamen = findViewById(R.id.btnSupprimerMedicament);

        // Configuration du RecyclerView
        recyclerViewMedicaments.setLayoutManager(new LinearLayoutManager(this));
        medicamentAdapter = new MedicamentAdapter(medicamentList);
        recyclerViewMedicaments.setAdapter(medicamentAdapter);

        Log.d("DEBUG_MEDICAMENT", "Adapter initialisé avec " + medicamentList.size() + " éléments");

        // Lancer l'activité de suppression
        btnSupprimerMedicamen.setOnClickListener(v -> {
            Intent intent = new Intent(this, SupprimmerMedicamentActivity.class);
            startActivityForResult(intent, 1);
        });

        // Lancer l'activité d'ajout
        btnAjouterMedicament.setOnClickListener(view -> {
            Intent intent = new Intent(RappelMedicamentActivity.this, AjoutMedicamentActivity.class);
            ajoutMedicamentLauncher.launch(intent);
        });
    }

    // ActivityResultLauncher pour AjoutMedicamentActivity
    private final androidx.activity.result.ActivityResultLauncher<Intent> ajoutMedicamentLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String nom = data.getStringExtra("nom");
                    String type = data.getStringExtra("type");
                    String heure = data.getStringExtra("heure");

                    if (nom != null && type != null && heure != null) {
                        Medicament nouveauMedicament = new Medicament(nom, type, heure);
                        medicamentList.add(nouveauMedicament);
                        medicamentAdapter.notifyItemInserted(medicamentList.size() - 1);

                        Log.d("DEBUG_MEDICAMENT", "Ajout : " + nom + " - " + type + " - " + heure);
                        Log.d("DEBUG_MEDICAMENT", "Liste mise à jour : " + medicamentList.size() + " éléments");
                    }
                }
            });

    // Gérer le retour de la page de suppression
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            medicamentAdapter.notifyDataSetChanged(); // Rafraîchir la liste
        }
    }
}

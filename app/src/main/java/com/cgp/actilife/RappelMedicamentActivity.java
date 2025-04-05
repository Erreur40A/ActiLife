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

    private RecyclerView recyclerViewMedicaments;
    private MedicamentAdapter medicamentAdapter;


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

        recyclerViewMedicaments.setLayoutManager(new LinearLayoutManager(this));
        medicamentAdapter = new MedicamentAdapter(medicamentList);
        recyclerViewMedicaments.setAdapter(medicamentAdapter);

        Log.d("DEBUG_MEDICAMENT", "Adapter initialisé avec " + medicamentList.size() + " éléments");

        btnSupprimerMedicamen.setOnClickListener(v -> {
            Intent intent = new Intent(this, SupprimmerMedicamentActivity.class);
            startActivityForResult(intent, 1);
        });

        btnAjouterMedicament.setOnClickListener(view -> {
            Intent intent = new Intent(RappelMedicamentActivity.this, AjoutMedicamentActivity.class);
            ajoutMedicamentLauncher.launch(intent);
        });
    }

    private final androidx.activity.result.ActivityResultLauncher<Intent> ajoutMedicamentLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<Medicament> nouveaux = (ArrayList<Medicament>) result.getData().getSerializableExtra("liste_medocs");

                    if (nouveaux != null && !nouveaux.isEmpty()) {
                        int debut = medicamentList.size();
                        medicamentList.addAll(nouveaux);
                        medicamentAdapter.notifyItemRangeInserted(debut, nouveaux.size());

                        Log.d("DEBUG_MEDICAMENT", "Ajout graupé de " + nouveaux.size() + " éléments.");
                        Log.d("DEBUG_MEDICAMENT", "Nouvelle taille totale : " + medicamentList.size());
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            medicamentAdapter.notifyDataSetChanged();
        }
    }
}

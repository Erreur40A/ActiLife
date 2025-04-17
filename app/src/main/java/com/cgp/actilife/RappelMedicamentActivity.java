package com.cgp.actilife;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RappelMedicamentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMedicaments;
    private MedicamentAdapter medicamentAdapter;


    public static List<Medicament> medicamentList = new ArrayList<>();

    private Button btnAjouterMedicament;
    private Button btnSupprimerMedicamen;


    private List<Medicament> chargerMedicamentsDepuisBDD() {
        DatabaseOpenHelper db = new DatabaseOpenHelper(this);
        List<Map<String, String>> enregistrements = db.getAll(ConstDB.MEDICAMENTS);

        List<Medicament> liste = new ArrayList<>();

        for (Map<String, String> ligne : enregistrements) {
            String nom = ligne.get(ConstDB.MEDICAMENTS_NOM);
            String heuresConcatenees = ligne.get(ConstDB.MEDICAMENTS_HEURES_PRISE);

            if (heuresConcatenees != null && !heuresConcatenees.isEmpty()) {
                String[] heures = heuresConcatenees.split(",");

                for (String heure : heures) {
                    liste.add(new Medicament(nom, heure.trim()));
                }
            }
        }

        return liste;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rappel_medicament);

        medicamentList = chargerMedicamentsDepuisBDD();

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
                            //  Enregistrement dans la BDD
                        DatabaseOpenHelper db = new DatabaseOpenHelper(this);

                        // Regrouper les heures par nom
                        Map<String, List<String>> mapNomVersHeures = new HashMap<>();

                        for (Medicament medicament : nouveaux) {
                            String nom = medicament.getNom();
                            String heure = medicament.getHeure();

                            if (!mapNomVersHeures.containsKey(nom)) {
                                mapNomVersHeures.put(nom, new ArrayList<>());
                            }

                            mapNomVersHeures.get(nom).add(heure);
                        }

                        // ✅ Insérer une seule fois chaque médicament avec ses heures concaténées
                        for (Map.Entry<String, List<String>> entry : mapNomVersHeures.entrySet()) {
                            String nom = entry.getKey();
                            String heuresConcat = TextUtils.join(",", entry.getValue());

                            Map<String, Object> fields = new HashMap<>();
                            fields.put(ConstDB.MEDICAMENTS_NOM, nom);
                            fields.put(ConstDB.MEDICAMENTS_HEURES_PRISE, heuresConcat);

                            db.insertData(ConstDB.MEDICAMENTS, fields);
                        }

                        // Vider l'ancienne liste d'affichage
                        medicamentList.clear();

                        //  Recharger depuis la BDD en splittant les heures
                        medicamentList.addAll(chargerMedicamentsDepuisBDD());

                        // Notifier le RecyclerView
                        medicamentAdapter.notifyDataSetChanged();


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
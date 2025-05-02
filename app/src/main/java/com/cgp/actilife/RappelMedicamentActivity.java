package com.cgp.actilife;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
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

    //ImageView btnderetour = findViewById(R.id.btnRetour);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
            }
        }

        //Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.SECOND, 10); // alarme dans 1 minute

        /*AlarmScheduler.setAlarm(
                this,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                LesNotifications.RAPPEL_MEDICAMENT
        );*/


        setContentView(R.layout.activity_rappel_medicament);

        // ✅ Test d'envoi manuel de l'alarme
        Intent testIntent = new Intent(this, AlarmReceiver.class);
        testIntent.setAction("com.cgp.actilife.ALARME_MEDICAMENT");
        testIntent.putExtra("type_notif", LesNotifications.RAPPEL_MEDICAMENT);
        sendBroadcast(testIntent);

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
        //btnderetour.setOnClickListener(v->finish());

        btnAjouterMedicament.setOnClickListener(view -> {
            Intent intent = new Intent(RappelMedicamentActivity.this, AjoutMedicamentActivity.class);
            ajoutMedicamentLauncher.launch(intent);
        });
        planifierAlarmesMedicaments();
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
                        planifierAlarmesMedicaments();

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


    private void planifierAlarmesMedicaments() {
        DatabaseOpenHelper db = new DatabaseOpenHelper(this);
        List<Map<String, String>> enregistrements = db.getAll(ConstDB.MEDICAMENTS);

        Calendar now = Calendar.getInstance();

        for (Map<String, String> ligne : enregistrements) {
            String nom = ligne.get(ConstDB.MEDICAMENTS_NOM);
            String heures = ligne.get(ConstDB.MEDICAMENTS_HEURES_PRISE);

            if (heures != null && !heures.isEmpty()) {
                String[] tableauHeures = heures.split(",");

                for (String heure : tableauHeures) {
                    String[] hm = heure.trim().split(":");
                    if (hm.length == 2) {
                        int h = Integer.parseInt(hm[0]);
                        int m = Integer.parseInt(hm[1]);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, h);
                        cal.set(Calendar.MINUTE, m);
                        cal.set(Calendar.SECOND, 0);

                        // Si l’heure est déjà passée aujourd’hui, on planifie pour demain
                        if (cal.before(now)) {
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        Log.d("DEBUG_ALARME_MEDOC", "Planification : " + nom + " à " + h + "h" + m);

                        AlarmScheduler.setAlarm(this,
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                LesNotifications.RAPPEL_MEDICAMENT);
                    }
                }
            }
        }
    }

}

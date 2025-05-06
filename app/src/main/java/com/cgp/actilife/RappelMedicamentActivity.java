package com.cgp.actilife;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RappelMedicamentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMedicaments;
    private MedicamentAdapter medicamentAdapter;

    public static List<Medicament> medicamentList = new ArrayList<>();
    //private final ArrayList<String> heures = new ArrayList<>();

    private Button btnAjouterMedicament;
    private Button btnSupprimerMedicamen;
    private PopUp pop_up_ajout_medoc;
    private PopUp pop_up_suppr_medoc;

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
        db.close();
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

        setContentView(R.layout.activity_rappel_medicament);

        medicamentList = chargerMedicamentsDepuisBDD();

        recyclerViewMedicaments = findViewById(R.id.recyclerViewMedicaments);
        btnAjouterMedicament = findViewById(R.id.btnAjouterMedicament);
        btnSupprimerMedicamen = findViewById(R.id.btnSupprimerMedicament);

        recyclerViewMedicaments.setLayoutManager(new LinearLayoutManager(this));
        medicamentAdapter = new MedicamentAdapter(medicamentList);
        recyclerViewMedicaments.setAdapter(medicamentAdapter);

        ImageView btnRetour = findViewById(R.id.btnRetour);
        btnRetour.setOnClickListener(v->finish());

        pop_up_suppr_medoc = new PopUp(this, R.layout.popup_suppression_medoc);
        setPopUpSupprMedocListener(pop_up_suppr_medoc);

        btnSupprimerMedicamen.setOnClickListener(v -> {
            pop_up_suppr_medoc.show();
        });

        pop_up_ajout_medoc = new PopUp(this, R.layout.popup_ajout_medicament);
        setPopUpAjoutMedocListener(pop_up_ajout_medoc);

        btnAjouterMedicament.setOnClickListener(view -> {
            pop_up_ajout_medoc.show();
        });
        planifierAlarmesMedicaments();
    }

    private void setPopUpAjoutMedocListener(PopUp popUp){
        EditText editNom = popUp.getView(R.id.nomMedicament);
        LinearLayout layoutHeuresAjoutees = popUp.getView(R.id.layoutHeuresAjoutees);
        ArrayList<String> heures = new ArrayList<>();
        DatabaseOpenHelper db = new DatabaseOpenHelper(this);
        Map<String, Object> fields = new HashMap<>();

        popUp.setOnClickListener(R.id.heureMedicament, v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Utilise un style classique avec spinner (défilement)
            TimePickerDialog picker = new TimePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (view, selectedHour, selectedMinute) -> {
                        final String heure = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        final String nomMedoc = editNom.getText().toString().trim();

                        if (nomMedoc.isEmpty()) {
                            Toast.makeText(this, "Entrez le nom du médicament d'abord", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 🟢 Vérification dans la liste temporaire
                        if (!heures.contains(heure)) {
                            heures.add(heure);
                            TextView tv = new TextView(popUp.context);
                            tv.setText(String.format("• %s ", heure));
                            tv.setTextSize(18);
                            layoutHeuresAjoutees.addView(tv);
                        } else {
                            Toast.makeText(this, "Heure déjà ajoutée", Toast.LENGTH_SHORT).show();
                        }
                    },
                    hour,
                    minute,
                    true
            );

            Log.d("les heures edit text heure", heures.toString());
            // Important : applique un fond transparent pour un effet plus fluide
            picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            picker.show();
        });

        popUp.setOnClickListener(R.id.btnRetour1, v -> {
            editNom.getText().clear();
            int nbenfant = layoutHeuresAjoutees.getChildCount();

            for (int i = 0; i < nbenfant; i++) {
                TextView view = (TextView) layoutHeuresAjoutees.getChildAt(i);
                view.setText("");
            }

            heures.clear();

            popUp.dismiss();
        });

        popUp.setOnClickListener(R.id.btnAjouterMedicament2, v -> {
            String nom = editNom.getText().toString().trim();

            if (TextUtils.isEmpty(nom) || heures.isEmpty()) {
                Toast.makeText(this, "Remplis tous les champs et ajoute au moins une heure", Toast.LENGTH_SHORT).show();
                return;
            }

            String lesHeures = String.join(", ", heures);
            fields.put(ConstDB.MEDICAMENTS_HEURES_PRISE, lesHeures);
            fields.put(ConstDB.MEDICAMENTS_NOM, nom);
            db.insertData(ConstDB.MEDICAMENTS, fields);

            Log.d("les heures bouton ajout medoc", heures.toString());

            for (String horaire: heures) {
                String heure = horaire.split(":")[0];
                String minute = horaire.split(":")[1];

                Calendar calendar = Calendar.getInstance();

                AlarmScheduler.setAlarm(
                        this,
                        calendar.get(Calendar.DAY_OF_MONTH),
                        Integer.parseInt(heure),
                        Integer.parseInt(minute),
                        LesNotifications.RAPPEL_MEDICAMENT);
            }

            medicamentAdapter.addMedicament(new Medicament(nom, lesHeures));

            editNom.getText().clear();
            int nbenfant = layoutHeuresAjoutees.getChildCount();

            for (int i = 0; i < nbenfant; i++) {
                TextView view = (TextView) layoutHeuresAjoutees.getChildAt(i);
                view.setText("");
            }

            heures.clear();
            popUp.dismiss();
        });

        db.close();
    }

    private void setPopUpSupprMedocListener(PopUp popUp){
        RecyclerView recyclerView = popUp.getView(R.id.recyclerViewMedicaments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MedicamentSuppressionAdapter(medicamentList, () -> {
            medicamentAdapter.notifyDataSetChanged();
        }));

        popUp.setOnClickListener(R.id.btnRetourDeSuppression, v -> {
            popUp.dismiss();
        });
    }

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

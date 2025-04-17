package com.cgp.actilife;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AjoutMedicamentActivity extends AppCompatActivity {

    private final ArrayList<String> heures = new ArrayList<>();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Directement appeler la méthode qui affiche la pop-up
        afficherPopupAjoutMedicament();
    }

    private void afficherPopupAjoutMedicament() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.PopUpArrondi);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_ajout_medicament, null);
        builder.setView(popupView);

        // Crée la boîte de dialogue
        dialog = builder.create();

        // Récupération des champs
        EditText editNom = popupView.findViewById(R.id.nomMedicament);
        EditText editHeure = popupView.findViewById(R.id.heureMedicament);
        LinearLayout layoutHeuresAjoutees = popupView.findViewById(R.id.layoutHeuresAjoutees);

        // Ouvre le TimePicker
        editHeure.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);

            // Utilise un style classique avec spinner (défilement)
            TimePickerDialog picker = new TimePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    (view, selectedHour, selectedMinute) -> {
                        String heure = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        String nomMedoc = editNom.getText().toString().trim();

                        if (TextUtils.isEmpty(nomMedoc)) {
                            Toast.makeText(this, "Entrez le nom du médicament d'abord", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // 🔍 Vérification dans la BDD si le médicament avec cette heure existe déjà
                        DatabaseOpenHelper db = new DatabaseOpenHelper(this);
                        List<Map<String, String>> enregistrements = db.getAll(ConstDB.MEDICAMENTS);
                        for (Map<String, String> ligne : enregistrements) {
                            String nomExistant = ligne.get(ConstDB.MEDICAMENTS_NOM);
                            String heuresEnregistrees = ligne.get(ConstDB.MEDICAMENTS_HEURES_PRISE); // ex: 08:00,12:00

                            if (nomExistant.equalsIgnoreCase(nomMedoc) && heuresEnregistrees != null) {
                                String[] heuresDeja = heuresEnregistrees.split(",");
                                for (String h : heuresDeja) {
                                    if (h.trim().equals(heure)) {
                                        Toast.makeText(this, "Ce médicament est déjà prévu à cette heure", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }
                        }

                        // 🟢 Vérification dans la liste temporaire
                        if (!heures.contains(heure)) {
                            heures.add(heure);
                            TextView tv = new TextView(this);
                            tv.setText("• " + heure);
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


            // Important : applique un fond transparent pour un effet plus fluide
            picker.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            picker.show();
        });

        // Annuler = fermer la boîte
        Button btnAnnuler = popupView.findViewById(R.id.btnRetour1);
        btnAnnuler.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            dialog.dismiss();
            finish();
        });

        // Ajouter = valider les champs
        Button btnAjouter = popupView.findViewById(R.id.btnAjouterMedicament2);
        btnAjouter.setOnClickListener(v -> {
            String nom = editNom.getText().toString().trim();

            if (TextUtils.isEmpty(nom) || heures.isEmpty()) {
                Toast.makeText(this, "Remplis tous les champs et ajoute au moins une heure", Toast.LENGTH_SHORT).show();
                return;
            }

            // Préparer les objets à retourner
            ArrayList<Medicament> resultList = new ArrayList<>();
            for (String h : heures) {
                resultList.add(new Medicament(nom, h));
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("liste_medocs", resultList);
            setResult(RESULT_OK, resultIntent);

            dialog.dismiss();
            finish();
        });

        dialog.setCancelable(false);
        dialog.show();
    }
}

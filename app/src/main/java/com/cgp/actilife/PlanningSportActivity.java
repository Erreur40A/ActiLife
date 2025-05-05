package com.cgp.actilife;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanningSportActivity extends AppCompatActivity {

    private final ArrayList<Activite> listeActivitesSportives = new ArrayList<>();
    private RecyclerView recyclerView;
    private ActiviteAdapter activiteAdapter;
    private int lastId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_sport);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnAjouter = findViewById(R.id.btnAjouterActivite);
        Button btnSupprimer = findViewById(R.id.btnSupprimerActivites);
        ImageView btnRetour = findViewById(R.id.btnRetour);
        CardView conteneurRecycler = findViewById(R.id.conteneurRecycler);
        TextView messageMotivation = findViewById((R.id.messageMotivation));

        DatabaseOpenHelper db = new DatabaseOpenHelper(this);

        messageMotivation.setText(db.getMotivation(ConstDB.MOTIVATIONS_TYPE_SPORT));

       btnRetour.setOnClickListener(view -> finish());

        List<Map<String, String >> allActivites = db.getAll(ConstDB.ACTIVITES_SPORTIVES);
        if (!allActivites.isEmpty()){
            allActivites.forEach((map-> {
                Activite activite = new Activite(
                        Integer.parseInt(map.get(ConstDB.ACTIVITES_SPORTIVES_ID)),
                        map.get(ConstDB.ACTIVITES_SPORTIVES_NOM),
                        map.get(ConstDB.ACTIVITES_SPORTIVES_HEURE_DEBUT),
                        map.get(ConstDB.ACTIVITES_SPORTIVES_HEURE_FIN),
                        new ArrayList<>(List.of(map.get(ConstDB.ACTIVITES_SPORTIVES_JOURS_ACTIFS).split(";")))
                );
                listeActivitesSportives.add(activite);
            }));
            Map<String, String> lastActivity = allActivites.get(allActivites.size() - 1);
            lastId = Integer.parseInt(lastActivity.get(ConstDB.ACTIVITES_SPORTIVES_ID));
        }

        //test de rappel medicament au btn retour
//        btnRetour.setOnClickListener(view -> {
//            Intent intent = new Intent(PlanningSportActivity.this, RappelMedicamentActivity.class);
//            startActivity(intent);
//            finish(); // Optionnel : ferme la page actuelle pour ne pas empiler les écrans
//        });


        PopUp pop_up_ajout_activite = new PopUp(this, R.layout.popup_ajout_activite);


        btnAjouter.setOnClickListener(view -> pop_up_ajout_activite.show());

        pop_up_ajout_activite.setOnClickListener(R.id.heureDebutActivite, v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            new TimePickerDialog(PlanningSportActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view, h, m) -> {
                String heure = String.format("%02d:%02d", h, m);
                EditText editHeureDebut = pop_up_ajout_activite.getView(R.id.heureDebutActivite);
                editHeureDebut.setText(heure);
            }, hour, minute, true).show();
        });

        pop_up_ajout_activite.setOnClickListener(R.id.heureFinActivite, v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            new TimePickerDialog(PlanningSportActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view, h, m) -> {
                String heure = String.format("%02d:%02d", h, m);
                EditText editHeureFin = pop_up_ajout_activite.getView(R.id.heureFinActivite);
                editHeureFin.setText(heure);
            }, hour, minute, true).show();
        });

        pop_up_ajout_activite.setOnClickListener(R.id.jourActivite, v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(
                    PlanningSportActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateChoisie = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        GridLayout layoutJoursAjoutees = pop_up_ajout_activite.getView(R.id.layoutJoursAjoutees);

                        TextView nouveauJour = new TextView(PlanningSportActivity.this);
                        nouveauJour.setText(dateChoisie);
                        nouveauJour.setTextColor(Color.WHITE);
                        nouveauJour.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor));
                        nouveauJour.setPadding(16, 8, 16, 8);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.setMargins(8, 8, 8, 8);
                        nouveauJour.setLayoutParams(params);

                        layoutJoursAjoutees.addView(nouveauJour);
                    },
                    year, month, day
            ).show();
        });

        //lorsqu'on valide les ajouts (backend a gerer ici)

        pop_up_ajout_activite.setOnClickListener(R.id.btnAjouterAcivite2, view -> {
            GridLayout layoutJoursAjoutees = pop_up_ajout_activite.getView(R.id.layoutJoursAjoutees);
            EditText nomInput = pop_up_ajout_activite.getView(R.id.nomActivite);
            EditText heureDebutInput = pop_up_ajout_activite.getView(R.id.heureDebutActivite);
            EditText heureFinInput = pop_up_ajout_activite.getView(R.id.heureFinActivite);

            String nom = nomInput.getText().toString().trim();
            String heureDebut = heureDebutInput.getText().toString().trim();
            String heureFin = heureFinInput.getText().toString().trim();

            if (nom.isEmpty() || heureDebut.isEmpty() || heureFin.isEmpty()) {
                Toast.makeText(PlanningSportActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                return;
            }
            //traiter les jours ajoutés et les ajouter a une liste
            List<String> joursSelectionnes = new ArrayList<>();
            for (int i = 0; i < layoutJoursAjoutees.getChildCount(); i++) {
                View child = layoutJoursAjoutees.getChildAt(i);
                if (child instanceof TextView) {
                    String jour = ((TextView) child).getText().toString();
                    if (!jour.isEmpty()) {
                        joursSelectionnes.add(jour);
                    }
                }
            }
            //verifier si un ajout y est déjà dans la liste
            for (Activite act : listeActivitesSportives) {
                if (act.getNom().equalsIgnoreCase(nom)) {
                    for (String jour : joursSelectionnes) {
                        if (act.getJours().contains(jour) && act.getHeureDebut().equals(heureDebut)) {
                            Toast.makeText(PlanningSportActivity.this,
                                    "Cette activité est déjà planifiée ce jour-là à la même heure.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }
            // dès que tout est bon ?
            lastId ++;
            Activite nouvelle = new Activite(lastId, nom, heureDebut, heureFin, joursSelectionnes);
            Map<String, Object> activityFields = new HashMap<>();
            activityFields.put(ConstDB.ACTIVITES_SPORTIVES_ID, lastId);
            activityFields.put(ConstDB.ACTIVITES_SPORTIVES_NOM, nom);
            activityFields.put(ConstDB.ACTIVITES_SPORTIVES_HEURE_DEBUT, heureDebut);
            activityFields.put(ConstDB.ACTIVITES_SPORTIVES_HEURE_FIN, heureFin);
            activityFields.put(ConstDB.ACTIVITES_SPORTIVES_JOURS_ACTIFS, String.join(";", joursSelectionnes));
            db.insertData(ConstDB.ACTIVITES_SPORTIVES, activityFields);
            listeActivitesSportives.add(nouvelle);

            int i = 0;
            for (String jour : nouvelle.getJours()){
                // Nouvelle partie : Planifier une alarme pour chaque jour et chaque activité
                try {
                    // Convertir le "jour" en date
                    String[] dateParts = jour.split("/");
                    int day = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]) - 1; // Attention : Calendar.MONTH est de 0 à 11
                    int year = Integer.parseInt(dateParts[2]);

                    // Convertir l'heure de début (exemple "14:30") en heures et minutes
                    String[] heureParts = nouvelle.getHeureDebut().split(":");
                    int heure = Integer.parseInt(heureParts[0]);
                    int minute = Integer.parseInt(heureParts[1]);

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    cal.set(Calendar.HOUR_OF_DAY, heure);
                    cal.set(Calendar.MINUTE, minute);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);

                    // Décaler 5 minutes avant l'heure de début
                    cal.add(Calendar.MINUTE, -5);

                    if (cal.before(Calendar.getInstance())) {
                        cal.add(Calendar.DAY_OF_YEAR, 1); // Si l'heure est déjà passée aujourd'hui
                    }

                    if(i==0){
                        AlarmScheduler.setAlarm(
                                getApplicationContext(),
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                LesNotifications.BIENTOT_HEURE_SPORT
                        );
                    }else{
                        AlarmScheduler.setAlarm(
                                getApplicationContext(),
                                cal.get(Calendar.DAY_OF_MONTH),
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                LesNotifications.PROCHAINE_ACTIVITE_SPORTIF
                        );
                    }

                } catch (Exception e) {
                    Log.e("AlarmSetup", "Erreur lors de la création de l'alarme pour l'activité : " + nouvelle.getNom() + " jour: " + jour, e);
                }
                i++;
            }

            // Mise à jour du RecyclerView
            ArrayList<Pair<Activite, String>> nouvelleListeAPlat = new ArrayList<>();
            for (Activite act : listeActivitesSportives) {
                for (String jour : act.getJours()) {
                    nouvelleListeAPlat.add(new Pair<>(act, jour));
                }
            }

            activiteAdapter.updateData(nouvelleListeAPlat);

            // Réinitialisation UI
            nomInput.setText("");
            heureDebutInput.setText("");
            heureFinInput.setText("");
            layoutJoursAjoutees.removeAllViews();

            conteneurRecycler.setVisibility(View.VISIBLE);
            pop_up_ajout_activite.dismiss();
            Toast.makeText(PlanningSportActivity.this, "Activité ajoutée : " + nom, Toast.LENGTH_SHORT).show();
        });

        recyclerView = findViewById(R.id.recyclerViewActivitesSportives);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Toujours initialiser, même s’il n’y a rien
        ArrayList<Pair<Activite, String>> listDePair = new ArrayList<>();
        for (Activite act : listeActivitesSportives) {
            for (String jour : act.getJours()) {
                listDePair.add(new Pair<>(act, jour));
            }
        }
        activiteAdapter = new ActiviteAdapter(listDePair);
        recyclerView.setAdapter(activiteAdapter);

        // Gère la visibilité du conteneur après coup
        if (listDePair.isEmpty()) {
            conteneurRecycler.setVisibility(View.GONE);
        } else {
            conteneurRecycler.setVisibility(View.VISIBLE);
        }


        pop_up_ajout_activite.setOnClickListener(R.id.btnRetour2, v -> pop_up_ajout_activite.dismiss());

        //supimer une activité de la liste
        //l'affichage du pop up de suppression est géré dans SupprimerActiviteAdapter, pas necessaire pour le backend
        btnSupprimer.setOnClickListener(v -> {
            PopUp popupSupprimer = new PopUp(this, R.layout.popup_suppression_activite);
            CardView conteneurRecyclerSuppr = popupSupprimer.getView(R.id.conteneurRecyclerSuppression);

            RecyclerView recyclerViewSupprimer = popupSupprimer.getView(R.id.recyclerViewSuppressionActivitesSportives);
            recyclerViewSupprimer.setLayoutManager(new LinearLayoutManager(this));
            
            //  On "déplie" les activités avec leurs jours (ex : activité A sur 3 jours = 3 lignes)
            ArrayList<Pair<Activite, String>> activitesAvecJours = new ArrayList<>();
            for (Activite act : listeActivitesSportives) {
                for (String jour : act.getJours()) {
                    activitesAvecJours.add(new Pair<>(act, jour));
                }
            }

            SupprimerActiviteAdapter adapter = new SupprimerActiviteAdapter(
                    activitesAvecJours,
                    (activite, jour) -> {
                        activite.getJours().remove(jour);
                        if (activite.getJours().isEmpty()) {
                            listeActivitesSportives.remove(activite);
                        }
                        Map<String, String> fields = db.getOneWithId(ConstDB.ACTIVITES_SPORTIVES, activite.getId());
                        String joursActifs = fields.get(ConstDB.ACTIVITES_SPORTIVES_JOURS_ACTIFS);
                        if (joursActifs.contains(jour)){
                            joursActifs = joursActifs.replace(jour + ";", "").replace(jour, "");
                        }
                        if (joursActifs.isEmpty()){
                            db.effacerEnregistrement(ConstDB.ACTIVITES_SPORTIVES, activite.getId());
                        } else{
                            Map<String, Object> fieldToUpdate = new HashMap<>();
                            fieldToUpdate.put(ConstDB.ACTIVITES_SPORTIVES_JOURS_ACTIFS, joursActifs);
                            db.updateTableWithId(ConstDB.ACTIVITES_SPORTIVES, fieldToUpdate, activite.getId());
                        }


                        // Met à jour les données de la liste principale
                        ArrayList<Pair<Activite, String>> nouvelleListe = new ArrayList<>();
                        for (Activite act : listeActivitesSportives) {
                            for (String j : act.getJours()) {
                                nouvelleListe.add(new Pair<>(act, j));
                            }
                        }
                        activiteAdapter.updateData(nouvelleListe);
                        recyclerViewSupprimer.getAdapter().notifyDataSetChanged();

                        /*if (listeActivitesSportives.isEmpty()) {
                            conteneurRecycler.setVisibility(View.GONE);
                            conteneurRecyclerSuppr.setVisibility(View.GONE);
                        }*/
                        if (listeActivitesSportives.isEmpty()) {
                            recyclerViewSupprimer.setVisibility(View.GONE);

                        }
                    }
            );

            recyclerViewSupprimer.setAdapter(adapter);
            popupSupprimer.setOnClickListener(R.id.btnRetourDeSuppressionActivite, v1 -> popupSupprimer.dismiss());
            popupSupprimer.show();
        });
    }
}

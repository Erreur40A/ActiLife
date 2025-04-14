    package com.cgp.actilife;

    import android.util.Pair;
    import android.app.DatePickerDialog;
    import android.app.TimePickerDialog;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.recyclerview.widget.DividerItemDecoration;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;

    public class PlanningSportActivity extends AppCompatActivity {

        private final ArrayList<Activite> listeActivitesSportives = new ArrayList<>();
        private RecyclerView recyclerView;
        private ActiviteAdapter activiteAdapter;


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

            //pour l'ajout d'une activte dans la liste

            PopUp pop_up_ajout_activite = new PopUp(this, R.layout.popup_ajout_activite);
            CardView conteneurRecycler = findViewById(R.id.conteneurRecycler);


            btnAjouter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pop_up_ajout_activite.show();
                }
            });

            pop_up_ajout_activite.setOnClickListener(R.id.heureDebutActivite, v -> {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);

                new TimePickerDialog(PlanningSportActivity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view, h, m) -> {
                    String heure = String.format("%02d:%02d", h, m);
                    EditText editHeureDebut = pop_up_ajout_activite.getView(R.id.heureDebutActivite);
                    editHeureDebut.setText(heure);
                }, hour, minute, true).show();
            });

            pop_up_ajout_activite.setOnClickListener(R.id.heureFinActivite, v -> {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);

                new TimePickerDialog(PlanningSportActivity.this,android.R.style.Theme_Holo_Light_Dialog_NoActionBar, (view, h, m) -> {
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

                            // On ajoute la date à layoutJoursAjoutees dynamiquement
                            LinearLayout layoutJoursAjoutees = pop_up_ajout_activite.getView(R.id.layoutJoursAjoutees);
                            TextView nouveauJour = new TextView(PlanningSportActivity.this);
                            nouveauJour.setText(dateChoisie);
                            nouveauJour.setTextColor(getResources().getColor(android.R.color.black));
                            nouveauJour.setPadding(16, 8, 16, 8);

                            layoutJoursAjoutees.addView(nouveauJour);
                        },
                        year, month, day
                ).show();
            });




            pop_up_ajout_activite.setOnClickListener(R.id.btnAjouterAcivite2, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout layoutJoursAjoutees = pop_up_ajout_activite.getView(R.id.layoutJoursAjoutees);
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

                    // 1. Vérifier s’il y a un doublon jour + heureDebut pour le même nom
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

                    // 2. Sinon on ajoute comme une nouvelle activité
                    Activite nouvelle = new Activite(nom, heureDebut, heureFin, joursSelectionnes);
                    listeActivitesSportives.add(nouvelle);

                    // Vider les champs
                    nomInput.setText("");
                    heureDebutInput.setText("");
                    heureFinInput.setText("");
                    layoutJoursAjoutees.removeAllViews();

                    conteneurRecycler.setVisibility(View.VISIBLE);
                    pop_up_ajout_activite.dismiss();
                    Toast.makeText(PlanningSportActivity.this, "Activité ajoutée : " + nom, Toast.LENGTH_SHORT).show();
                    activiteAdapter.notifyItemInserted(listeActivitesSportives.size() - 1);
                }
            });


            recyclerView = findViewById(R.id.recyclerViewActivitesSportives);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            activiteAdapter = new ActiviteAdapter(listeActivitesSportives);
            recyclerView.setAdapter(activiteAdapter);

            DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(divider);


            pop_up_ajout_activite.setOnClickListener(R.id.btnRetour2, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pop_up_ajout_activite.dismiss(); // Ferme la popup
                }
            });

            btnSupprimer.setOnClickListener(v -> {
                PopUp popupSupprimer = new PopUp(this, R.layout.popup_suppression_activite);
                CardView conteneurRecyclerSuppr = popupSupprimer.getView(R.id.conteneurRecyclerSuppression);

                RecyclerView recyclerViewSupprimer = popupSupprimer.getView(R.id.recyclerViewSuppressionActivitesSportives);
                recyclerViewSupprimer.setLayoutManager(new LinearLayoutManager(this));

                DividerItemDecoration dividerSuppr = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                recyclerViewSupprimer.addItemDecoration(dividerSuppr);

                //  Construction de la liste activité-jour
                ArrayList<Pair<Activite, String>> activitesAvecJours = new ArrayList<>();
                for (Activite act : listeActivitesSportives) {
                    for (String jour : act.getJours()) {
                        activitesAvecJours.add(new Pair<>(act, jour));
                    }
                }

                SupprimerActiviteAdapter adapter = new SupprimerActiviteAdapter(
                        activitesAvecJours,
                        (activite, jour) -> {
                            // 🔁 Suppression du jour dans l'activité
                            activite.getJours().remove(jour);

                            // ❌ Si plus de jour = on enlève toute l'activité
                            if (activite.getJours().isEmpty()) {
                                listeActivitesSportives.remove(activite);
                            }

                            // 🔄 Rafraîchissement des deux RecyclerViews
                            activiteAdapter.notifyDataSetChanged();
                            recyclerViewSupprimer.getAdapter().notifyDataSetChanged();

                            // Si plus d'éléments visibles = on masque le bloc
                            if (listeActivitesSportives.isEmpty()) {
                                conteneurRecycler.setVisibility(View.GONE);
                                conteneurRecyclerSuppr.setVisibility(View.GONE);
                            }
                        }
                );

                recyclerViewSupprimer.setAdapter(adapter);

                popupSupprimer.setOnClickListener(R.id.btnRetourDeSuppressionActivite, v1 -> popupSupprimer.dismiss());
                popupSupprimer.show();
            });


        }
    }
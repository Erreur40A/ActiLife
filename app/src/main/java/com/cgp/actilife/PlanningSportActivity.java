    package com.cgp.actilife;


    import android.app.TimePickerDialog;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
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

                new TimePickerDialog(PlanningSportActivity.this, (view, h, m) -> {
                    String heure = String.format("%02d:%02d", h, m);
                    EditText editHeureDebut = pop_up_ajout_activite.getView(R.id.heureDebutActivite);
                    editHeureDebut.setText(heure);
                }, hour, minute, true).show();
            });

            pop_up_ajout_activite.setOnClickListener(R.id.heureFinActivite, v -> {
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);

                new TimePickerDialog(PlanningSportActivity.this, (view, h, m) -> {
                    String heure = String.format("%02d:%02d", h, m);
                    EditText editHeureFin = pop_up_ajout_activite.getView(R.id.heureFinActivite);
                    editHeureFin.setText(heure);
                }, hour, minute, true).show();
            });



            pop_up_ajout_activite.setOnClickListener(R.id.btnAjouterAcivite2, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText nomInput = pop_up_ajout_activite.getView(R.id.nomActivite);
                    EditText heureDebutInput = pop_up_ajout_activite.getView(R.id.heureDebutActivite);
                    EditText heureFinInput = pop_up_ajout_activite.getView(R.id.heureFinActivite);

                    String nom = nomInput.getText().toString().trim();
                    String heureDebut = heureDebutInput.getText().toString().trim();
                    String heureFin = heureFinInput.getText().toString().trim();

                    nomInput.setText("");
                    heureDebutInput.setText("");
                    heureFinInput.setText("");

                    if (nom.isEmpty() || heureDebut.isEmpty() || heureFin.isEmpty()) {
                        Toast.makeText(PlanningSportActivity.this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    Activite activite = new Activite(nom, heureDebut, heureFin);
                    listeActivitesSportives.add(activite);

                    // Afficher le bloc avec bordure si on ajoute une activité
                    conteneurRecycler.setVisibility(View.VISIBLE);



                    pop_up_ajout_activite.dismiss();

                    // Optionnel : tu peux afficher un message ou mettre à jour une liste affichée
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

            btnSupprimer.setOnClickListener(v ->{
                PopUp popupSupprimer = new PopUp(this, R.layout.popup_suppression_activite);
                CardView conteneurRecyclerSuppr = popupSupprimer.getView(R.id.conteneurRecyclerSuppression);


                RecyclerView recyclerViewSupprimer = popupSupprimer.getView(R.id.recyclerViewSuppressionActivitesSportives);
                recyclerViewSupprimer.setLayoutManager(new LinearLayoutManager(this));

                DividerItemDecoration dividerSuppr = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                recyclerViewSupprimer.addItemDecoration(dividerSuppr);


                final SupprimerActiviteAdapter[] adapterRef = new SupprimerActiviteAdapter[1]; // obligé car adapter est utilisé dans l'écouteur

                SupprimerActiviteAdapter adapter = new SupprimerActiviteAdapter(
                        listeActivitesSportives,
                        new SupprimerActiviteAdapter.OnDeleteClickListener() {
                            @Override
                            public void onDeleteClick(int position) {
                                listeActivitesSportives.remove(position);
                                adapterRef[0].notifyItemRemoved(position);
                                activiteAdapter.notifyDataSetChanged();

                                if (listeActivitesSportives.isEmpty()) {
                                    conteneurRecycler.setVisibility(View.GONE);
                                    conteneurRecyclerSuppr.setVisibility(View.GONE);
                                }


                            }
                        }
                );

                adapterRef[0] = adapter; // On initialise ici, après la classe anonyme

                recyclerViewSupprimer.setAdapter(adapter);

                popupSupprimer.setOnClickListener(R.id.btnRetourDeSuppressionActivite, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupSupprimer.dismiss(); // Ferme la popup
                    }
                });


                popupSupprimer.show();
            });

        }
    }
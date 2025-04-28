package com.cgp.actilife;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.activity.EdgeToEdge;
import android.widget.*;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.InputType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaloriesActivity extends AppCompatActivity {
    private static class FoodItem {
        private final String name;
        private final int calories;
        private final int quantity;

        public FoodItem(String name, int calories, int quantity) {
            this.name = name;
            this.calories = calories;
            this.quantity = quantity;
        }

        public String getName() { return name; }
        public int getCalories() { return calories; }
        public int getQuantity() { return quantity; }
    }
    private ProgressBar progressCalories;
    private TextView textProgressPercent;
    private TextView nbCaloriesTextView;
    private DatabaseOpenHelper dbHelper;
    private List<FoodItem> foodItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);

        // Initialisation des vues
        new Thread(() -> {
            try {
                Map<String, String> userdata = dbHelper.getOneWithoutId(ConstDB.USERDATA);
                if (userdata != null && userdata.containsKey(ConstDB.USERDATA_TAILLE_CM) && userdata.containsKey(ConstDB.USERDATA_POIDS_CIBLE)) {
                    String tailleStr = userdata.get(ConstDB.USERDATA_TAILLE_CM);
                    String poidsStr = userdata.get(ConstDB.USERDATA_POIDS_CIBLE);
                    String dateNaissanceStr = userdata.get(ConstDB.USERDATA_DATE_NAISSANCE); // Il faut aussi récupérer la date de naissance pour calculer l'âge !

                    if (tailleStr != null && poidsStr != null && dateNaissanceStr != null) {
                        int taille = Integer.parseInt(tailleStr);
                        int poids = Integer.parseInt(poidsStr);
                        int age = calculerAge(dateNaissanceStr);

                        int caloriesNecessaires = (int) (10 * poids + 6.25 * taille - 5 * age - 78);

                        // Mettre à jour dans la base de données
                        Map<String, Object> updateFields = new HashMap<>();
                        updateFields.put(ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI, caloriesNecessaires);
                        dbHelper.updateTableWithoutId(ConstDB.CALORIES, updateFields);

                        // Et mettre à jour l'affichage
                        runOnUiThread(() -> {
                            nbCaloriesTextView.setText(String.valueOf(caloriesNecessaires));
                            updateProgressBar(caloriesNecessaires);
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        progressCalories = findViewById(R.id.progressCalories);
        textProgressPercent = findViewById(R.id.textProgressPercentC);
        nbCaloriesTextView = findViewById(R.id.nb_cal);
        dbHelper = new DatabaseOpenHelper(this);

        Button btnValider = findViewById(R.id.btnValider);
        btnValider.setOnClickListener(v -> {
            String caloriesNecessaires = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_CALORIES_NECESSAIRES_PAR_JOUR);

            if (caloriesNecessaires != null && !caloriesNecessaires.isEmpty()) {
                // Exemple : afficher dans un Toast
                Toast.makeText(this, "Calories nécessaires : " + caloriesNecessaires, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Aucune donnée trouvée.", Toast.LENGTH_SHORT).show();
            }
        });

        // Chargement des calories enregistrées
        loadCaloriesFromDatabase();

        // Gestion des insets système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation des plats par défaut
        initializeSampleFoodItems();

        // Gestion du bouton retour
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> finish());

        // Bouton "Ajouter un plat"
        Button btnAjouterPlat = findViewById(R.id.btnAjouterPlat);
        btnAjouterPlat.setOnClickListener(v -> showAddFoodPopup());

        // Gestion du texte "Qu'avez-vous mangé ?"
        TextView textDefil = findViewById(R.id.text_defil);
        textDefil.setOnClickListener(v -> showFoodListPopup());

        // Forcer l'input de quantité à être un nombre
        EditText inputQuantite = findViewById(R.id.inputQuantite);
        inputQuantite.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void loadCaloriesFromDatabase() {
        new Thread(() -> {
            try {
                String caloriesStr = dbHelper.getAttributeWithoutId(
                        ConstDB.CALORIES,
                        ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI
                );

                runOnUiThread(() -> {
                    if (caloriesStr != null && !caloriesStr.isEmpty()) {
                        nbCaloriesTextView.setText(caloriesStr);
                    } else {
                        nbCaloriesTextView.setText("0");
                    }
                });
            } catch (Exception e) {
                Log.e("Calories", "Erreur de chargement", e);
                runOnUiThread(() -> nbCaloriesTextView.setText("Erreur"));
            }
        }).start();
    }

    private void updateCaloriesDisplay(int newCalories) {
        nbCaloriesTextView.setText(String.valueOf(newCalories));
        updateProgressBar(newCalories);

        new Thread(() -> {
            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put(ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI, newCalories);
            dbHelper.updateTableWithoutId(ConstDB.CALORIES, updateFields);
        }).start();
    }

    private void updateProgressBar(int calories) {
        int progress = (int) (((float) calories / 2000) * 100); // 2000 = besoin quotidien
        progressCalories.setProgress(progress);
        textProgressPercent.setText(progress + "%");
    }

    private void initializeSampleFoodItems() {
        foodItems.add(new FoodItem("Pomme", 52, 100));
        foodItems.add(new FoodItem("Poulet grillé", 165, 100));
        foodItems.add(new FoodItem("Pâtes", 131, 100));
        foodItems.add(new FoodItem("Salade César", 350, 100));
    }

    private void showAddFoodPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.activity_add_food, null);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        EditText nomPlat = popupView.findViewById(R.id.NomPlat);
        EditText nbCal = popupView.findViewById(R.id.nbcal);
        EditText quantite = popupView.findViewById(R.id.editQuant);
        Button btnAjouter = popupView.findViewById(R.id.btnAjouter);
        Button btnAnnuler = popupView.findViewById(R.id.btnAnnuler);

        // ➡️ Ici, seulement une seule fois btnAjouter.setOnClickListener
        btnAjouter.setOnClickListener(v -> {
            String nom = nomPlat.getText().toString();
            String calStr = nbCal.getText().toString();
            String quantStr = quantite.getText().toString();

            if (nom.isEmpty() || calStr.isEmpty() || quantStr.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int calories = Integer.parseInt(calStr);
                int quant = Integer.parseInt(quantStr);

                // ➔ Ajouter dans la liste locale
                foodItems.add(new FoodItem(nom, calories, quant));

                // ➔ Ajouter dans la base de données (table repas)
                Map<String, Object> foodData = new HashMap<>();
                foodData.put(ConstDB.REPAS_NOM, nom);
                foodData.put(ConstDB.REPAS_CALORIES , calories);
                foodData.put(ConstDB.REPAS_QUANTITE_G, quant);

                dbHelper.insertData(ConstDB.REPAS, foodData);

                Toast.makeText(this, "Plat ajouté avec succès", Toast.LENGTH_SHORT).show();

                int currentCalories = Integer.parseInt(nbCaloriesTextView.getText().toString());
                int newCalories = currentCalories + calories;
                updateCaloriesDisplay(newCalories);

                dialog.dismiss();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Veuillez entrer des nombres valides", Toast.LENGTH_SHORT).show();
            }
        });

        btnAnnuler.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void showFoodListPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_food_list, null);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        Spinner foodSpinner = popupView.findViewById(R.id.food_spinner);

        List<String> foodNames = new ArrayList<>();
        for (FoodItem item : foodItems) {
            foodNames.add(item.getName() + " - " + item.getCalories() + "kcal/" + item.getQuantity() + "g");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                foodNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodSpinner.setAdapter(adapter);

        Button btnSelect = popupView.findViewById(R.id.btn_select_food);
        btnSelect.setOnClickListener(v -> {
            int selectedPosition = foodSpinner.getSelectedItemPosition();
            if (selectedPosition >= 0 && selectedPosition < foodItems.size()) {
                FoodItem selectedFood = foodItems.get(selectedPosition);

                // ➡️ Remplir le TextView text_defil
                TextView textDefil = findViewById(R.id.text_defil);
                textDefil.setText(selectedFood.getName());

                // ➡️ Remplir le champ inputQuantite
                EditText inputQuantite = findViewById(R.id.inputQuantite);
                inputQuantite.setText(String.valueOf(selectedFood.getQuantity()));
            }
            dialog.dismiss();
        });

        dialog.show(); // ← Ici et pas à l'intérieur du setOnClickListener !
    }


}

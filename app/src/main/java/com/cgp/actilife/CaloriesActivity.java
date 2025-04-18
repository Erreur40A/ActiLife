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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaloriesActivity extends AppCompatActivity {
    private ProgressBar progressCalories;
    private TextView textProgressPercent;
    private DatabaseOpenHelper dbHelper;

    private TextView nbCaloriesTextView;
    private List<FoodItem> foodItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);
        progressCalories = findViewById(R.id.progressCalories);
        textProgressPercent = findViewById(R.id.textProgressPercentC);
        nbCaloriesTextView = findViewById(R.id.nb_cal); // Liez le TextView
        dbHelper = new DatabaseOpenHelper(this);

        loadCaloriesFromDatabase();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialisation de quelques exemples de plats
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

        // Gestion de l'edit text quantité
        EditText inputQuantite = findViewById(R.id.inputQuantite);
        inputQuantite.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);




    }

    private void loadCaloriesFromDatabase() {
        new Thread(() -> {
            try {
                // Récupération depuis la DB
                String caloriesStr = dbHelper.getAttributeWithoutId(
                        ConstDB.CALORIES,
                        ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI
                );

                // Mise à jour UI sur le thread principal
                runOnUiThread(() -> {
                    if (caloriesStr != null && !caloriesStr.isEmpty()) {
                        nbCaloriesTextView.setText(caloriesStr);
                    } else {
                        nbCaloriesTextView.setText("0"); // Valeur par défaut
                    }
                });
            } catch (Exception e) {
                Log.e("Calories", "Erreur de chargement", e);
                runOnUiThread(() -> nbCaloriesTextView.setText("Erreur"));
            }
        }).start();
    }

    private void updateCaloriesDisplay(int newCalories) {
        // Mettre à jour le TextView
        nbCaloriesTextView.setText(String.valueOf(newCalories));

        // Mettre à jour la progress bar
        updateProgressBar(newCalories);

        // Mettre à jour la base de données
        new Thread(() -> {
            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put(ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI, newCalories);
            dbHelper.updateTableWithoutId(ConstDB.CALORIES, updateFields);
        }).start();
    }
    private void initializeSampleFoodItems() {
        foodItems.add(new FoodItem("Pomme", 52, 100));
        foodItems.add(new FoodItem("Poulet grillé", 165, 100));
        foodItems.add(new FoodItem("Pâtes", 131, 100));
        foodItems.add(new FoodItem("Salade César", 350, 100));
    }
    private void updateProgressBar(int calories) {
        int progress = (int) (((float) calories / 2000) * 100); // 2000 = daily need
        progressCalories.setProgress(progress);
        textProgressPercent.setText(progress + "%");
    }

    private void showAddFoodPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popupView = LayoutInflater.from(this).inflate(R.layout.activity_add_food, null);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        // Récupération des vues
        EditText nomPlat = popupView.findViewById(R.id.NomPlat);
        EditText nbCal = popupView.findViewById(R.id.nbcal);
        EditText quantite = popupView.findViewById(R.id.editQuant);
        Button btnAjouter = popupView.findViewById(R.id.btnAjouter);
        Button btnAnnuler = popupView.findViewById(R.id.btnAnnuler);



        btnAjouter.setOnClickListener(v -> {
            String nom = nomPlat.getText().toString();
            String calStr = nbCal.getText().toString();
            String quantStr = quantite.getText().toString();

            if(nom.isEmpty() || calStr.isEmpty() || quantStr.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int calories = Integer.parseInt(calStr);
                int quant = Integer.parseInt(quantStr);

                // Ajout du nouveau plat à la liste
                foodItems.add(new FoodItem(nom, calories, quant));

                Toast.makeText(this, "Plat ajouté avec succès", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

                // Mettre à jour la liste des plats si nécessaire
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Veuillez entrer des nombres valides", Toast.LENGTH_SHORT).show();
            }

            try {
                int calories = Integer.parseInt(calStr);
                int quant = Integer.parseInt(quantStr);

                // Calcul des nouvelles calories
                int currentCalories = Integer.parseInt(nbCaloriesTextView.getText().toString());
                int newCalories = currentCalories + calories;

                // Mise à jour de l'affichage
                updateCaloriesDisplay(newCalories);

            } catch (NumberFormatException e) {
                // ... gestion d'erreur ...
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

        // Création de la liste des noms de plats pour le Spinner
        List<String> foodNames = new ArrayList<>();
        for (FoodItem item : foodItems) {
            foodNames.add(item.getName() + " - " + item.getCalories() + "kcal/" + item.getQuantity() + "g");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, foodNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodSpinner.setAdapter(adapter);

        Button btnSelect = popupView.findViewById(R.id.btn_select_food);
        btnSelect.setOnClickListener(v -> {
            int selectedPosition = foodSpinner.getSelectedItemPosition();
            if (selectedPosition >= 0 && selectedPosition < foodItems.size()) {
                FoodItem selectedFood = foodItems.get(selectedPosition);
                EditText inputQuantite = findViewById(R.id.inputQuantite);
                inputQuantite.setText(String.valueOf(selectedFood.getQuantity()));
                // Vous pouvez aussi mettre à jour d'autres champs si nécessaire
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    // Classe interne pour représenter un plat
    private static class FoodItem {
        private String name;
        private int calories;
        private int quantity;

        public FoodItem(String name, int calories, int quantity) {
            this.name = name;
            this.calories = calories;
            this.quantity = quantity;
        }

        public String getName() { return name; }
        public int getCalories() { return calories; }
        public int getQuantity() { return quantity; }
    }
}
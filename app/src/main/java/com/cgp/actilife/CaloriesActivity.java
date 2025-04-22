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
        progressCalories = findViewById(R.id.progressCalories);
        textProgressPercent = findViewById(R.id.textProgressPercentC);
        nbCaloriesTextView = findViewById(R.id.nb_cal);
        dbHelper = new DatabaseOpenHelper(this);

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

                foodItems.add(new FoodItem(nom, calories, quant));
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
                EditText inputQuantite = findViewById(R.id.inputQuantite);
                inputQuantite.setText(String.valueOf(selectedFood.getQuantity()));
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    // Classe interne représentant un plat
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
}

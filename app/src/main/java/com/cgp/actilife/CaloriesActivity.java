package com.cgp.actilife;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
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

        // Initialiser d'abord dbHelper et les vues
        dbHelper = new DatabaseOpenHelper(this);
        progressCalories = findViewById(R.id.progressCalories);
        textProgressPercent = findViewById(R.id.textProgressPercentC);
        nbCaloriesTextView = findViewById(R.id.nb_cal);

        calculerEtMettreAJourCaloriesNecessaires();

        String caloriesAjd = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI);
        if (caloriesAjd == null || caloriesAjd.isEmpty()) {
            Map<String, Object> init = new HashMap<>();
            init.put(ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI, 0);
            dbHelper.updateTableWithoutId(ConstDB.CALORIES, init);
        }


        // Bouton "Valider"
        Button btnValider = findViewById(R.id.btnValider);
        TextView textDefil = findViewById(R.id.text_defil);

        btnValider.setOnClickListener(v -> {
            String nomPlat = textDefil.getText().toString().trim();

            if (nomPlat.isEmpty() || nomPlat.equals("Qu’avez-vous mangé ?")) {
                Toast.makeText(this, "Aucun plat sélectionné", Toast.LENGTH_SHORT).show();
                return;
            }

            int caloriesDuPlat = 0;
            List<Map<String, String>> repasList = dbHelper.getAll(ConstDB.REPAS);

            for (Map<String, String> repas : repasList) {
                String nom = repas.get(ConstDB.REPAS_NOM);
                if (nom != null && nom.equalsIgnoreCase(nomPlat)) {
                    try {
                        caloriesDuPlat = Integer.parseInt(repas.get(ConstDB.REPAS_CALORIES));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            if (caloriesDuPlat == 0) {
                Toast.makeText(this, "Plat introuvable dans la base", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentStr = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI);
            Log.i("Calorie T", "currentStr =" + currentStr);
            int currentValue = currentStr != null && !currentStr.trim().isEmpty() ? Integer.parseInt(currentStr.trim()) : 0;

            int total = currentValue + caloriesDuPlat;

            // Mise à jour en base
            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put(ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI, total);
            dbHelper.updateTableWithoutId(ConstDB.CALORIES, updateFields);

            // Mise à jour UI
            String caloriesNecessairesStr = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_CALORIES_NECESSAIRES_PAR_JOUR);
            int caloriesNecessaires = caloriesNecessairesStr != null && !caloriesNecessairesStr.trim().isEmpty() ? Integer.parseInt(caloriesNecessairesStr.trim()) : 0;

            int caloriesRestantes = Math.max(0, caloriesNecessaires - total);

            // Mise à jour du TextView avec les calories restantes
            nbCaloriesTextView.setText(String.valueOf(caloriesRestantes));

            updateProgressBar();

            Toast.makeText(this, nomPlat + " ajouté. Total aujourd'hui : " + total + " kcal", Toast.LENGTH_SHORT).show();
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
        ImageView backArrow = findViewById(R.id.btnRetour);
        backArrow.setOnClickListener(v -> finish());

        // Bouton "Ajouter un plat"
        Button btnAjouterPlat = findViewById(R.id.btnAjouterPlat);
        btnAjouterPlat.setOnClickListener(v -> showAddFoodPopup());

        // Gestion du texte "Qu'avez-vous mangé ?"
        textDefil.setOnClickListener(v -> showFoodListPopup());

        // Forcer l'input de quantité à être un nombre
        EditText inputQuantite = findViewById(R.id.inputQuantite);
        inputQuantite.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    private void loadCaloriesFromDatabase() {
        try {
            String caloriesStr = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_CALORIES_NECESSAIRES_PAR_JOUR);
            int caloriesNecessaires = caloriesStr != null && !caloriesStr.isEmpty() ? Integer.parseInt(caloriesStr) : 0;

            String caloriesAjdStr = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI);
            int caloriesAjd = caloriesAjdStr != null && !caloriesAjdStr.isEmpty() ? Integer.parseInt(caloriesAjdStr) : 0;

            int caloriesRestantes = Math.max(0, caloriesNecessaires - caloriesAjd);

            nbCaloriesTextView.setText(String.valueOf(caloriesRestantes));
        } catch (Exception e) {
            e.printStackTrace();
            nbCaloriesTextView.setText("Erreur");
        }
    }

    private void updateCaloriesDisplay(int newCalories) {
        updateProgressBar();

        Map<String, Object> updateFields = new HashMap<>();
        Log.i("Calorie T", "updateCaloriesDisplay newCalories =" + newCalories );
        updateFields.put(ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI, newCalories);
        dbHelper.updateTableWithoutId(ConstDB.CALORIES, updateFields);

    }

    private void updateProgressBar() {
        String caloriesNecessairesStr = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_CALORIES_NECESSAIRES_PAR_JOUR);
        String caloriesConsommeesStr = dbHelper.getAttributeWithoutId(ConstDB.CALORIES, ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI);
        Log.i("Calorie T", "caloriesConsommeesStr" + caloriesConsommeesStr);

        int caloriesNecessaires = caloriesNecessairesStr != null && !caloriesNecessairesStr.isEmpty() ? Integer.parseInt(caloriesNecessairesStr) : 2000;
        int caloriesConsommees = caloriesConsommeesStr != null && !caloriesConsommeesStr.isEmpty() ? Integer.parseInt(caloriesConsommeesStr) : 0;

        int progress = caloriesNecessaires > 0 ? (int) (((float) caloriesConsommees / caloriesNecessaires) * 100) : 0;
        progress = Math.max(0, Math.min(progress, 100));

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
            String nom = nomPlat.getText().toString().trim();
            String calStr = nbCal.getText().toString().trim();
            String quantStr = quantite.getText().toString().trim();

            if (nom.isEmpty() || calStr.isEmpty() || quantStr.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int calories = Integer.parseInt(calStr);
                int quant = Integer.parseInt(quantStr);

                foodItems.add(new FoodItem(nom, calories, quant));

                Map<String, Object> foodData = new HashMap<>();
                foodData.put(ConstDB.REPAS_NOM, nom);
                foodData.put(ConstDB.REPAS_CALORIES, calories);
                foodData.put(ConstDB.REPAS_QUANTITE_G, quant);

                dbHelper.insertData(ConstDB.REPAS, foodData);

                Toast.makeText(this, "Plat ajouté dans la base de données", Toast.LENGTH_SHORT).show();

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
        List<Map<String, String>> repasList = dbHelper.getAll(ConstDB.REPAS);

        List<String> foodNames = new ArrayList<>();
        List<FoodItem> databaseFoodItems = new ArrayList<>();
        List<Integer> repasIds = new ArrayList<>();

        for (Map<String, String> repas : repasList) {
            String nom = repas.get(ConstDB.REPAS_NOM);
            int calories = Integer.parseInt(repas.get(ConstDB.REPAS_CALORIES));
            int quantite = Integer.parseInt(repas.get(ConstDB.REPAS_QUANTITE_G));
            int id = Integer.parseInt(repas.get(ConstDB.REPAS_ID));

            databaseFoodItems.add(new FoodItem(nom, calories, quantite));
            foodNames.add(nom + " - " + calories + "kcal/" + quantite + "g");
            repasIds.add(id);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, foodNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodSpinner.setAdapter(adapter);

        Button btnSelect = popupView.findViewById(R.id.btn_select_food);
        Button btnDelete = popupView.findViewById(R.id.btn_suppr_food);

        btnSelect.setOnClickListener(v -> {
            int selectedPosition = foodSpinner.getSelectedItemPosition();
            if (selectedPosition >= 0 && selectedPosition < databaseFoodItems.size()) {
                FoodItem selectedFood = databaseFoodItems.get(selectedPosition);

                TextView textDefil = findViewById(R.id.text_defil);
                textDefil.setText(selectedFood.getName());

                EditText inputQuantite = findViewById(R.id.inputQuantite);
                inputQuantite.setText(String.valueOf(selectedFood.getQuantity()));

                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(v -> {
            int selectedPosition = foodSpinner.getSelectedItemPosition();
            if (selectedPosition >= 0 && selectedPosition < repasIds.size()) {
                int idToDelete = repasIds.get(selectedPosition);
                dbHelper.effacerEnregistrement(ConstDB.REPAS, idToDelete);
                Toast.makeText(this, "Plat supprimé avec succès", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private int calculerAge(String dateNaissanceStr) {
        try {
            String[] parts = dateNaissanceStr.split("-");
            int anneeNaissance = Integer.parseInt(parts[0]);
            int moisNaissance = Integer.parseInt(parts[1]);
            int jourNaissance = Integer.parseInt(parts[2]);

            java.util.Calendar today = java.util.Calendar.getInstance();
            int anneeActuelle = today.get(java.util.Calendar.YEAR);
            int moisActuel = today.get(java.util.Calendar.MONTH) + 1;
            int jourActuel = today.get(java.util.Calendar.DAY_OF_MONTH);

            int age = anneeActuelle - anneeNaissance;
            if (moisActuel < moisNaissance || (moisActuel == moisNaissance && jourActuel < jourNaissance)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            e.printStackTrace();
            return 25;
        }
    }

    private void calculerEtMettreAJourCaloriesNecessaires() {

        try {
            Map<String, String> userdata = dbHelper.getOneWithoutId(ConstDB.USERDATA);

            int taille = 170;
            int poids = 80;
            int age = 25;

            if (userdata != null) {
                String tailleStr = userdata.get(ConstDB.USERDATA_TAILLE_CM);
                String poidsStr = userdata.get(ConstDB.USERDATA_POIDS_CIBLE);
                String dateNaissanceStr = userdata.get(ConstDB.USERDATA_DATE_NAISSANCE);

                if (tailleStr != null && !tailleStr.isEmpty()) taille = Integer.parseInt(tailleStr);
                if (poidsStr != null && !poidsStr.isEmpty()) poids = Integer.parseInt(poidsStr);
                if (dateNaissanceStr != null && !dateNaissanceStr.isEmpty()) age = calculerAge(dateNaissanceStr);
            }

            int caloriesNecessaires = (int) (10 * poids + 6.25 * taille - 5 * age - 78);

            Map<String, Object> updateFields = new HashMap<>();
            updateFields.put(ConstDB.CALORIES_CALORIES_NECESSAIRES_PAR_JOUR, caloriesNecessaires);
            dbHelper.updateTableWithoutId(ConstDB.CALORIES, updateFields);

            updateProgressBar();
            loadCaloriesFromDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

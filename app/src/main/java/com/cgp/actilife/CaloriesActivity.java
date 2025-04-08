package com.cgp.actilife;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.activity.EdgeToEdge;

public class CaloriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calories);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Afficher la popup
        showAddFoodPopup();
    }

    private void showAddFoodPopup() {
        PopUp pop = new PopUp(this,R.layout.activity_add_food);

        pop.setOnClickListener(R.id.btnAjouter, v -> {

            // Récupérer les éléments de la popup
            EditText nomPlat = pop.getView(R.id.NomPlat);
            EditText nbCal = pop.getView(R.id.nbcal);
            EditText quantite = pop.getView(R.id.editQuant);

            // Gérer le bouton "Ajouter"

            String nom = nomPlat.getText().toString();
            String cal = nbCal.getText().toString();
            String quant = quantite.getText().toString();

            // Exemple d'action
            Toast.makeText(getApplicationContext(), "Plat ajouté : " + nom + " quantité : " + quant + " cal : " + cal, Toast.LENGTH_SHORT).show();
            pop.dismiss();

        });
        pop.show();



    }
}

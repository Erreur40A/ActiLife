package com.cgp.actilife;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        ImageView iconSettings = findViewById(R.id.iconSettings);
        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer SecondActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        // Liste des menus avec leur activité associée
        // TODO changer par l'activite correspondante
        List<MenuItem> menuItems = Arrays.asList(
                new MenuItem(R.id.menu1, R.drawable.fire_solid, "Calories", CaloriesActivity.class),
                new MenuItem(R.id.menu2, R.drawable.shoe_prints_solid, "Nombre de pas", PasActivity.class),
                new MenuItem(R.id.menu3, R.drawable.weight_hanging_solid, "Suivi du poids", PoidsActivity.class),
                new MenuItem(R.id.menu4, R.drawable.person_running_solid, "Planning de sport", PlanningSportActivity.class),
                new MenuItem(R.id.menu5, R.drawable.prescription_bottle_medical_solid, "Rappel medocs", RappelMedicamentActivity.class),
                new MenuItem(R.id.menu6, R.drawable.bed_solid, "Rappel sommeil", RappelSommeilActivity.class),
                new MenuItem(R.id.menu7, R.drawable.bottle_water_solid, "Rappel hydratation", RappelHydratationActivity.class)
        );

        // Boucle pour configurer les menus dynamiquement
        for (MenuItem item : menuItems) {
            View menu = findViewById(item.viewId);
            ImageView icon = menu.findViewById(R.id.cardIcon);
            TextView text = menu.findViewById(R.id.cardText);

            icon.setImageResource(item.iconId);
            text.setText(item.text);

            // Ajouter un clic pour ouvrir l'activité correspondante
            menu.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, item.activityClass);
                startActivity(intent);
            });
        }
    }
}
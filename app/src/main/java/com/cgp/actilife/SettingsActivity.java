package com.cgp.actilife;

import android.os.Bundle;
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
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import android.widget.EditText;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText etNom = findViewById(R.id.et_nom).findViewById(R.id.edit_text);
        etNom.setHint("Nom");

        EditText etPrenom = findViewById(R.id.et_prenom).findViewById(R.id.edit_text);
        etPrenom.setHint("Prénom");

        EditText etAge = findViewById(R.id.et_age).findViewById(R.id.edit_text);
        etAge.setHint("Âge");

        EditText etPoids = findViewById(R.id.et_poids).findViewById(R.id.edit_text);
        etPoids.setHint("Poids");

    }
}
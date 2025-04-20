package com.cgp.actilife;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


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

        Context context = this;

        ImageView btnRetour = findViewById(R.id.iconBack);
        btnRetour.setOnClickListener(v -> finish());


        DatabaseOpenHelper db = new DatabaseOpenHelper(this);
        Map<String, String> userdata = db.getOneWithoutId(ConstDB.USERDATA);

        // =================== Récupération des champs ===================
        EditText etNom = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etNom)).findViewById(R.id.edit_text);
        EditText etPrenom = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etPrenom)).findViewById(R.id.edit_text);
        EditText etAge = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etAge)).findViewById(R.id.edit_text);
        EditText etPoids = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etPoids)).findViewById(R.id.edit_text);
        SwitchCompat switchHydratation = findViewById(R.id.switch_hydratation);

        if (etNom != null) {
            if (userdata.containsKey(ConstDB.USERDATA_NOM) && userdata.get(ConstDB.USERDATA_NOM) != null) {
                etNom.setText(userdata.get(ConstDB.USERDATA_NOM));
            } else {
                etNom.setHint("Nom");
            }
        }
        TextView helloText = findViewById(R.id.hello_name);
        if (etPrenom != null) {
            if (userdata.containsKey(ConstDB.USERDATA_PRENOM) && userdata.get(ConstDB.USERDATA_PRENOM) != null) {
                etPrenom.setText(userdata.get(ConstDB.USERDATA_PRENOM));
                String helloMessage =  getString(R.string.hello_with_name, userdata.get(ConstDB.USERDATA_PRENOM));
                helloText.setText(helloMessage);
            } else {
                helloText.setText(R.string.param_tres);
                etPrenom.setHint("Prénom");
            }
        }

        if (etAge != null) {
            if (userdata.containsKey(ConstDB.USERDATA_DATE_NAISSANCE) && userdata.get(ConstDB.USERDATA_DATE_NAISSANCE) != null) {
                etAge.setText(userdata.get(ConstDB.USERDATA_DATE_NAISSANCE));
            } else {
                etAge.setHint("Date de naissance");
            }
            etAge.setInputType(InputType.TYPE_NULL);
            etAge.setOnClickListener(v -> showDatePickerDialog(etAge));
        }

        if (etPoids != null) {
            if (userdata.containsKey(ConstDB.USERDATA_TAILLE_CM) && userdata.get(ConstDB.USERDATA_TAILLE_CM) != null) {
                etPoids.setText(userdata.get(ConstDB.USERDATA_TAILLE_CM ) + " cm");
            } else {
                etPoids.setHint("Taille");
            }
            etPoids.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        if (switchHydratation != null) {
            if (userdata.containsKey(ConstDB.USERDATA_RAPPEL_HYDRATATION_ACTIVE)) {
                String val = userdata.get(ConstDB.USERDATA_RAPPEL_HYDRATATION_ACTIVE);
                switchHydratation.setChecked("1".equals(val) || "true".equalsIgnoreCase(val));
            } else {
                switchHydratation.setChecked(false);  // ou true si tu veux activer par défaut
            }
        }

       // String prenomValue = etPrenom.getText().toString().trim();


        Button confirmBtn = findViewById((R.id.btn_ok));
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etNom = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etNom)).findViewById(R.id.edit_text);
                EditText etPrenom = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etPrenom)).findViewById(R.id.edit_text);
                EditText etAge = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etAge)).findViewById(R.id.edit_text);
                EditText etPoids = ((LinearLayout) findViewById(R.id.formCont).findViewById(R.id.etPoids)).findViewById(R.id.edit_text);
                SwitchCompat switchHydratation = findViewById(R.id.switch_hydratation);

                boolean isHydratationActive = switchHydratation.isChecked();
                boolean hasError = false;

                if (etNom.getText().toString().trim().isEmpty()) {
                    etNom.setError("Ce champ est requis");
                    hasError = true;
                }
                if (etPrenom.getText().toString().trim().isEmpty()) {
                    etPrenom.setError("Ce champ est requis");
                    hasError = true;
                }
                if (etAge.getText().toString().trim().isEmpty()) {
                    etAge.setError("Ce champ est requis");
                    hasError = true;
                }
                if (etPoids.getText().toString().trim().isEmpty()) {
                    etPoids.setError("Ce champ est requis");
                    hasError = true;
                }

                if (!hasError) {
                    // Crée le dictionnaire pour la mise à jour
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(ConstDB.USERDATA_NOM, etNom.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_PRENOM, etPrenom.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_DATE_NAISSANCE, etAge.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_TAILLE_CM, etPoids.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_RAPPEL_HYDRATATION_ACTIVE, isHydratationActive);
                    Map<String, String> userdata = db.getOneWithoutId(ConstDB.USERDATA);

                    // test getMotivation
                    // String mot = db.getMotivation(ConstDB.MOTIVATIONS_TYPE_SPORT);
                    // Log.i("test-motivation", mot);
                    if (userdata != null && ! userdata.isEmpty()){
                        db.updateTableWithoutId(ConstDB.USERDATA, fields);
                        if (!isHydratationActive){
                            RappelHydratationWorker.cancelAllAlarms(context);
                        }
                    }
                    else{
                        db.insertData(ConstDB.USERDATA, fields);
                    }

                    Toast.makeText(getApplicationContext(), "Informations enregistrées", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog(EditText etAge) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etAge.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

}
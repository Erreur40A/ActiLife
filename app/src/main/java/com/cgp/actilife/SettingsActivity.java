package com.cgp.actilife;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.PeriodicWorkRequest;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class SettingsActivity extends AppCompatActivity {

    int genre = 0;

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
        Spinner spinner_genre = findViewById(R.id.spinner_genre);

        if (etNom != null) {
            if (userdata.containsKey(ConstDB.USERDATA_NOM) && userdata.get(ConstDB.USERDATA_NOM) != null) {
                etNom.setText(userdata.get(ConstDB.USERDATA_NOM));
            } else {
                etNom.setHint("Saisir votre nom");
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
                etPrenom.setHint("Saisir votre prénom");
            }
        }

        if (etAge != null) {
            if (userdata.containsKey(ConstDB.USERDATA_DATE_NAISSANCE) && userdata.get(ConstDB.USERDATA_DATE_NAISSANCE) != null) {
                etAge.setText(userdata.get(ConstDB.USERDATA_DATE_NAISSANCE));
            } else {
                etAge.setHint("Saisir votre date de naissance");
            }
            etAge.setInputType(InputType.TYPE_NULL);
            etAge.setOnClickListener(v -> showDatePickerDialog(etAge));
        }

        if (etPoids != null) {
            if (userdata.containsKey(ConstDB.USERDATA_TAILLE_CM) && userdata.get(ConstDB.USERDATA_TAILLE_CM) != null) {
                etPoids.setText(userdata.get(ConstDB.USERDATA_TAILLE_CM ));
            } else {
                etPoids.setHint("Saisir votre taille");
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

        if (spinner_genre != null) {
            if (userdata.containsKey(ConstDB.USERDATA_SEXE)) {
                genre = Integer.parseInt(userdata.get(ConstDB.USERDATA_SEXE));
                spinner_genre.setSelection(genre); // Sélectionne "Homme" si genre est 0, "Femme" si genre est 1
            } else {
                spinner_genre.setSelection(genre); // Sélectionne "Homme" par défaut si la clé n'est pas présente
            }
        }


        spinner_genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               genre = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

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

               List< Map<String, String >> test = db.getAll(ConstDB.CALORIES);
                Log.d("Calories", test.toString());

                String current;
                if (!hasError) {
                    // Crée le dictionnaire pour la mise à jour
                    Map<String, Object> fields = new HashMap<>();
                    fields.put(ConstDB.USERDATA_NOM, etNom.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_PRENOM, etPrenom.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_DATE_NAISSANCE, etAge.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_TAILLE_CM, etPoids.getText().toString().trim());
                    fields.put(ConstDB.USERDATA_RAPPEL_HYDRATATION_ACTIVE, isHydratationActive);
                    fields.put(ConstDB.USERDATA_SEXE, genre);
                    Map<String, String> userdata = db.getOneWithoutId(ConstDB.USERDATA);

                    // test getMotivation
                    // String mot = db.getMotivation(ConstDB.MOTIVATIONS_TYPE_SPORT);
                    // Log.i("test-motivation", mot);
                    if (userdata != null && ! userdata.isEmpty()){
                        db.updateTableWithoutId(ConstDB.USERDATA, fields);

                    }
                    else{
                        db.insertData(ConstDB.USERDATA, fields);
                    }

                    if (!isHydratationActive){
                        cancelAllAlarms(context);
                    } else{
                        PeriodicWorkRequest rappelWorkRequest =
                                new PeriodicWorkRequest.Builder(RappelHydratationWorker.class, 1, TimeUnit.DAYS)
                                        .build();
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


        public static void cancelAllAlarms(Context context) {
        int[] heures = {9, 15, 20};

        for (int i = 0; i < heures.length; i++) {
            Intent intent = new Intent(context, AlarmReceiver.class); // ta classe de BroadcastReceiver
            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am != null) {
                am.cancel(pi);
            }

            Log.i("RappelWorker", "Alarm " + heures[i] + "h annulée");
        }
    }

}
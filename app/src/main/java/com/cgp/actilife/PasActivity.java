package com.cgp.actilife;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.*;

public class PasActivity extends AppCompatActivity implements SensorEventListener {

    private EditText inputQuantite_pas;
    private DatabaseOpenHelper db;
    private ProgressBar bar_pas;
    private TextView pct_bar_pas;
    private TextView text_bar_pas;
    private Button btnAjouterPas;
    private TextView texte_motivation_pas;

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;

    private int baseSteps = -1;
    private int currentSteps = 0;
    private String dateAujourdhui;
    private int objectifDuJour = 500;

    private Handler handler = new Handler();
    private Runnable delayedUpdate;
    private final long DELAI_ECRITURE = 5000;

    private SharedPreferences prefs;
    private static final String PREFS_NAME = "PasPrefs";
    private static final String KEY_BASE_STEPS = "base_steps";
    private static final String KEY_DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1001);
            }
        }

        setContentView(R.layout.activity_pas);

        ImageView btnRetour = findViewById(R.id.backArrow);
        btnRetour.setOnClickListener(v -> finish());

        db = new DatabaseOpenHelper(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        inputQuantite_pas = findViewById(R.id.inputQuantite_pas);
        bar_pas = findViewById(R.id.bar_pas);
        pct_bar_pas = findViewById(R.id.pct_bar_pas);
        text_bar_pas = findViewById(R.id.text_bar_pas);
        texte_motivation_pas = findViewById(R.id.texte_moitivation_pas);
        btnAjouterPas = findViewById(R.id.btn_para_pas);

        texte_motivation_pas.setText(db.getMotivation(ConstDB.MOTIVATIONS_TYPE_PAS));

        dateAujourdhui = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Initialiser les SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Vérifier la DB
        List<Map<String, String>> enregistrements = db.getAll(ConstDB.PAS);
        for (Map<String, String> ligne : enregistrements) {
            if (dateAujourdhui.equals(ligne.get(ConstDB.PAS_DATE_DU_JOUR))) {
                try {
                    String objectifStr = ligne.get(ConstDB.PAS_OBJECTIF_PAS);
                    if (objectifStr != null) objectifDuJour = Integer.parseInt(objectifStr);
                    String pasStr = ligne.get(ConstDB.PAS_NB_PAS_AUJOURDHUI);
                    if (pasStr != null) currentSteps = Integer.parseInt(pasStr);
                } catch (Exception ignored) {}
                break;
            }
        }

        updateUI(currentSteps, objectifDuJour);

        btnAjouterPas.setOnClickListener(v -> {
            String quantiteStr = inputQuantite_pas.getText().toString();
            if (!quantiteStr.isEmpty()) {
                int newGoal = Integer.parseInt(quantiteStr);
                objectifDuJour = newGoal;

                Map<String, String> dataExistante = db.getOneWithoutId(ConstDB.PAS);
                String dateExistante = dataExistante.get(ConstDB.PAS_DATE_DU_JOUR);

                Map<String, Object> fields = new HashMap<>();
                fields.put(ConstDB.PAS_DATE_DU_JOUR, dateAujourdhui);
                fields.put(ConstDB.PAS_OBJECTIF_PAS, newGoal);

                if (!dateAujourdhui.equals(dateExistante)) {
                    currentSteps = 0;
                    fields.put(ConstDB.PAS_NB_PAS_AUJOURDHUI, 0);
                    // On reset aussi la baseSteps car c'est un nouveau jour
                    prefs.edit().remove(KEY_BASE_STEPS).apply();
                } else {
                    fields.put(ConstDB.PAS_NB_PAS_AUJOURDHUI, currentSteps);
                }

                if (dataExistante.isEmpty() || dataExistante.get(ConstDB.PAS_DATE_DU_JOUR) == null) {
                    db.insertData(ConstDB.PAS, fields);
                } else {
                    db.updateTableWithoutId(ConstDB.PAS, fields);}
                updateUI(currentSteps, newGoal);

                texte_motivation_pas.setText(db.getMotivation(ConstDB.MOTIVATIONS_TYPE_PAS));

            } else {
                Toast.makeText(this, "Veuillez entrer un objectif", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null) sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) event.values[0];

            // Lire la valeur stockée
            String dateSaved = prefs.getString(KEY_DATE, "");
            int savedBaseSteps = prefs.getInt(KEY_BASE_STEPS, -1);

            if (!dateAujourdhui.equals(dateSaved) || savedBaseSteps == -1) {
                // Nouveau jour OU pas encore enregistré ➔ on stocke la valeur actuelle comme base
                prefs.edit()
                        .putString(KEY_DATE, dateAujourdhui)
                        .putInt(KEY_BASE_STEPS, totalSteps)
                        .apply();
                baseSteps = totalSteps;
                currentSteps = 0; // Redémarre à 0 au début d’une journée
            } else {
                baseSteps = savedBaseSteps;
                currentSteps = totalSteps - baseSteps;
            }

            if(delayedUpdate != null) handler.removeCallbacks(delayedUpdate);

            delayedUpdate = () ->{
                Map<String, Object> fields = new HashMap<>();
                fields.put(ConstDB.PAS_DATE_DU_JOUR, dateAujourdhui);
                fields.put(ConstDB.PAS_NB_PAS_AUJOURDHUI, currentSteps);
                db.updateTableWithoutId(ConstDB.PAS, fields);
            };

            handler.postDelayed(delayedUpdate, DELAI_ECRITURE);

            runOnUiThread(() -> updateUI(currentSteps, objectifDuJour));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ignoré
    }

    private void updateUI(int steps, int goal) {
        text_bar_pas.setText(String.format("Vous avez fait %d pas sur %d", steps, goal));
        int percent = goal > 0 ? (steps * 100) / goal : 0;
        bar_pas.setMax(100);
        bar_pas.setProgress(percent);
        pct_bar_pas.setText(percent + "%");
    }
}

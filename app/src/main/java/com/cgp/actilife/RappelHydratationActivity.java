package com.cgp.actilife;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class RappelHydratationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rappel_hydratation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseOpenHelper db = new DatabaseOpenHelper(this);

        Boolean rappelHydratation = Boolean.parseBoolean(db.getAttributeWithoutId(ConstDB.USERDATA,ConstDB.USERDATA_RAPPEL_HYDRATATION_ACTIVE));
        Log.i("Rappel Hydration", String.valueOf(rappelHydratation));

        if (rappelHydratation) {
            int[] heures = {9, 15, 21};

            for (int heure : heures) {
                Calendar calendrier = Calendar.getInstance();
                calendrier.set(Calendar.HOUR_OF_DAY, heure);
                calendrier.set(Calendar.MINUTE, 10);
                calendrier.set(Calendar.SECOND, 0);

                // Si l'heure est déjà passée aujourd'hui, on programme pour demain
                if (calendrier.before(Calendar.getInstance())) {
                    calendrier.add(Calendar.DAY_OF_YEAR, 1);
                }

                AlarmScheduler.setAlarm(
                        this,
                        calendrier.get(Calendar.DAY_OF_MONTH),
                        calendrier.get(Calendar.HOUR_OF_DAY),
                        calendrier.get(Calendar.MINUTE),
                        LesNotifications.RAPPEL_HYDRATATION // ou un autre identifiant si tu veux 3 types différents
                );
            }
        }

    }
}
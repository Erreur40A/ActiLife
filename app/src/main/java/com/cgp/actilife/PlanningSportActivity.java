package com.cgp.actilife;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class PlanningSportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_sport);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Exemple d'initialisation d'une notification
        Calendar dateDuJour = Calendar.getInstance();
        //la notif arrive 1min après le lancement de l'app
        dateDuJour.add(Calendar.MINUTE, 1);

        AlarmScheduler.setAlarm(
                this,
                dateDuJour.get(Calendar.DAY_OF_MONTH),
                dateDuJour.get(Calendar.HOUR_OF_DAY),
                dateDuJour.get(Calendar.MINUTE),
                LesNotifications.PROCHAINE_ACTIVITE_SPORTIF);
    }
    /*--------Fin exemple---------*/
}
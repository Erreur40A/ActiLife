package com.cgp.actilife;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class RappelSommeilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rappel_sommeil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        //Exemple d'initialisation d'une notification
        Calendar dateDuJour = Calendar.getInstance();
        //la notif arrive 1min après le lancement de l'app
        dateDuJour.add(Calendar.MINUTE, 1);

        AlarmScheduler.setAlarm(
                this,
                dateDuJour.get(Calendar.DAY_OF_MONTH),
                dateDuJour.get(Calendar.HOUR_OF_DAY),
                dateDuJour.get(Calendar.MINUTE),
                LesNotifications.RAPPEL_HEURE_COUCHER);
        /*--------Fin exemple---------*/
    }
}
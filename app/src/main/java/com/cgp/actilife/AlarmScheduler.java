package com.cgp.actilife;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmScheduler {
    public static void setAlarm(Context context, int jour, int heure, int minute, LesNotifications type_notif){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(context, "Veuillez activer l'autorisation des alarmes exactes.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                context.startActivity(intent);
                return;
            }
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("type_notif", type_notif);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, type_notif.ordinal(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, jour);
        date.set(Calendar.HOUR_OF_DAY, heure);
        date.set(Calendar.MINUTE, minute);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(), pendingIntent);

        String aff = "jour: " + jour + " heure: " + heure + "h" + minute;
        Log.d("Heure alarme", aff);
    }
}

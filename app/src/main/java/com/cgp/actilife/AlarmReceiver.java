package com.cgp.actilife;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notif_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        LesNotifications type_notif = (LesNotifications) intent.getSerializableExtra("type_notif");

        int idLayout = getLayoutNotif(type_notif);

        String idAndName = null;
        // A compléter dans les autres branches
        if (idLayout == -1) {
            //Ne devrai jamais arriver théoriquement
            throw new InternalError("Le type de notifications spécifier n'existe pas");
        } else if (idLayout == R.layout.notifcation_reveil || idLayout == R.layout.notification_couchez) {
            idAndName = "Rappel Sommeil";
            createChannel(notif_manager, idAndName, idAndName);
        } else if (idLayout == R.layout.notification_hydratation) {
            idAndName = "Rappel Hydratation";
            createChannel(notif_manager, idAndName, idAndName);
        } else if (idLayout == R.layout.notifications_faire_sport || idLayout == R.layout.notifications_prochaine_activite) {
            idAndName = "Sport";
            createChannel(notif_manager, idAndName, idAndName);
        }else if (idLayout == R.layout.notification_medicament) {
            idAndName = "Medicament";
            createChannel(notif_manager, idAndName, idAndName);
        }

        Intent intentActivity = new Intent(context, MainActivity.class);
        intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, type_notif.ordinal(), intentActivity, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        RemoteViews layout_notif = new RemoteViews(context.getPackageName(), idLayout);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, idAndName);

        builder.setSmallIcon(R.drawable.logoactilife)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setCustomContentView(layout_notif)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        notif_manager.notify(type_notif.ordinal(), builder.build());
    }

    public void createChannel(NotificationManager manager, String nameChannel, String idChannel){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(idChannel, nameChannel, NotificationManager.IMPORTANCE_HIGH);

            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes attribut_audio = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            //{pause à 0ms, vibration à 500ms, pause à 1000ms}
            long[] vibration = {0, 500, 1000};

            channel.enableVibration(true);
            channel.setVibrationPattern(vibration);
            channel.setSound(sound, attribut_audio);

            manager.createNotificationChannel(channel);
        }
    }

    public int getLayoutNotif(LesNotifications type){
        int layout;

        // A compléter dans les autres branches
        switch (type) {
            case RAPPEL_HEURE_COUCHER:
                layout = R.layout.notification_couchez;
                break;

            case RAPPEL_MEDICAMENT:
                layout = R.layout.notification_medicament;
                break;

            case RAPPEL_HYDRATATION:
                layout = R.layout.notification_hydratation;
                break;


            case BIENTOT_HEURE_SPORT:
                layout = R.layout.notifications_faire_sport;
                break;

            case RAPPEL_HEURE_REVEIL:
                layout = R.layout.notifcation_reveil;
                break;

            case PROCHAINE_ACTIVITE_SPORTIF:
                layout = R.layout.notifications_prochaine_activite;
                break;

            default:
                layout = -1;
                break;
        }
        return layout;
    }
}

package com.cgp.actilife;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notif_manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        LesNotifications type_notif = (LesNotifications) intent.getSerializableExtra("type_notif");

        int idLayout = getLayoutNotif(type_notif);

        String idAndName = null;
        // A compléter dans les autres branches
        if(idLayout == -1){
            //Ne devrai jamais arriver théoriquement
            throw new InternalError("Le type de notifications spécifier n'existe pas");
        } else if (idLayout == R.layout.notifications_prochaine_activite || idLayout == R.layout.notifications_faire_sport) {
            idAndName = "Sport";
            createChannel(notif_manager, idAndName, idAndName);
        }

        Intent intentActivity = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, type_notif.ordinal(), intentActivity, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Log.d("test", " " + R.layout.notifications_prochaine_activite);
        RemoteViews layout_notif = new RemoteViews(context.getPackageName(), idLayout);

        //Ne devrai jamais arriver théoriquement
        if(idAndName == null)
            throw new InternalError("L'id du channel n'existe pas");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, idAndName);

        builder.setSmallIcon(R.drawable.logoactilife)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setCustomContentView(layout_notif)
                .setContentIntent(pendingIntent);

        notif_manager.notify(type_notif.ordinal(), builder.build());
    }

    public void createChannel(NotificationManager manager, String nameChannel, String idChannel){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(idChannel, nameChannel, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
    }

    public int getLayoutNotif(LesNotifications type){
        int layout;

        // A compléter dans les autres branches
        switch (type) {
            case BIENTOT_HEURE_SPORT:
                layout = R.layout.notifications_faire_sport;
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

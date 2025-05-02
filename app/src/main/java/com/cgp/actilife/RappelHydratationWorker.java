package com.cgp.actilife;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
public class RappelHydratationWorker extends Worker {

    public RappelHydratationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        DatabaseOpenHelper db = new DatabaseOpenHelper(getApplicationContext());
        Boolean rappelHydratation = Boolean.parseBoolean(
                db.getAttributeWithoutId(ConstDB.USERDATA, ConstDB.USERDATA_RAPPEL_HYDRATATION_ACTIVE)
        );

        if (rappelHydratation) {
            int[] heures = {9, 15, 21};
            for (int heure : heures) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, heure);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                if (cal.before(Calendar.getInstance())) {
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                }

                AlarmScheduler.setAlarm(
                        getApplicationContext(),
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        LesNotifications.RAPPEL_HYDRATATION
                );

                Log.i("RappelWorker", "Alarm set for: " + cal.getTime());
            }
        }

        return Result.success();
    }

}

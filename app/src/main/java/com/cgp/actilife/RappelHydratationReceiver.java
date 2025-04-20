package com.cgp.actilife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class RappelHydratationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            PeriodicWorkRequest rappelWorkRequest =
                    new PeriodicWorkRequest.Builder(RappelHydratationWorker.class, 1, TimeUnit.DAYS)
                            .build();

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "rappel_hydratation_daily",
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    rappelWorkRequest
            );

            Log.i("BootReceiver", "WorkManager re-scheduled on boot.");
        }
    }
}

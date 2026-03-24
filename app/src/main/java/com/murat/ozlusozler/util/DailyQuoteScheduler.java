package com.murat.ozlusozler.util;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.murat.ozlusozler.worker.QuoteNotificationWorker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public final class DailyQuoteScheduler {

    private static final String TAG = "DailyQuoteScheduler";
    private static final String UNIQUE_WORK_NAME = "daily_quote_work_v2";
    private static final String LEGACY_WORK_NAME = "daily_quote_work";

    private DailyQuoteScheduler() {
    }

    public static void ensureScheduled(Context context) {
        enqueueWork(context, ExistingWorkPolicy.KEEP);
    }

    public static void reschedule(Context context) {
        enqueueWork(context, ExistingWorkPolicy.REPLACE);
    }

    private static void enqueueWork(Context context, ExistingWorkPolicy policy) {
        SettingsManager settingsManager = new SettingsManager(context);
        long initialDelay = calculateInitialDelay(
                settingsManager.getNotificationHour(),
                settingsManager.getNotificationMinute()
        );
        Log.d(TAG, "Scheduling quote work. policy=" + policy + ", delayMs=" + initialDelay);
        cancelLegacyWork(context);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(QuoteNotificationWorker.class)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .addTag(UNIQUE_WORK_NAME)
                .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniqueWork(
                        UNIQUE_WORK_NAME,
                        policy,
                        workRequest
                );
    }

    public static void cancel(Context context) {
        Log.d(TAG, "Cancelling quote work");
        WorkManager.getInstance(context.getApplicationContext())
                .cancelUniqueWork(UNIQUE_WORK_NAME);
        cancelLegacyWork(context);
    }

    private static void cancelLegacyWork(Context context) {
        WorkManager.getInstance(context.getApplicationContext()).cancelUniqueWork(LEGACY_WORK_NAME);
        WorkManager.getInstance(context.getApplicationContext()).cancelAllWorkByTag(LEGACY_WORK_NAME);
    }

    private static long calculateInitialDelay(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);

        if (!nextRun.isAfter(now)) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).toMillis();
    }
}

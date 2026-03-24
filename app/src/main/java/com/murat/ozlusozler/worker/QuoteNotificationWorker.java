package com.murat.ozlusozler.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.murat.ozlusozler.R;
import com.murat.ozlusozler.data.model.Quote;
import com.murat.ozlusozler.data.model.QuoteResponse;
import com.murat.ozlusozler.data.network.ApiClient;
import com.murat.ozlusozler.data.network.GeminiManager;
import com.murat.ozlusozler.util.CategoryMapper;
import com.murat.ozlusozler.util.DailyQuoteScheduler;
import com.murat.ozlusozler.util.NotificationHelper;
import com.murat.ozlusozler.util.SettingsManager;

import java.io.IOException;

import retrofit2.Response;

public class QuoteNotificationWorker extends Worker {

    private static final String TAG = "QuoteWorker";

    public QuoteNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SettingsManager settingsManager = new SettingsManager(context);
        Log.d(TAG, "Worker started");
        if (!NotificationHelper.hasNotificationPermission(context)) {
            Log.d(TAG, "Notification permission missing, worker exits");
            return Result.success();
        }

        try {
            Response<QuoteResponse> response = ApiClient.getQuoteApiService().getRandomQuote().execute();
            QuoteResponse body = response.body();

            if (!response.isSuccessful() || body == null || body.getQuote() == null || body.getQuote().trim().isEmpty()) {
                Log.e(TAG, "Quote API returned empty or unsuccessful response");
                if (settingsManager.isNotificationsEnabled()) {
                    DailyQuoteScheduler.reschedule(context);
                }
                return Result.success();
            }

            String author = body.getAuthor();
            if (author == null || author.trim().isEmpty()) {
                author = context.getString(R.string.unknown_author);
            }

            Quote quote = new Quote(
                    String.valueOf(body.getId()),
                    body.getQuote().trim(),
                    author,
                    CategoryMapper.CATEGORY_GENERAL
            );

            if (SettingsManager.NOTIFICATION_LANGUAGE_TR.equals(settingsManager.getNotificationLanguage())) {
                GeminiManager geminiManager = new GeminiManager();
                if (geminiManager.hasApiKey()) {
                    String prompt = context.getString(R.string.gemini_translate_prompt_template, quote.getOriginalText());
                    String translated = geminiManager.generateTextSync(prompt);
                    quote.setTranslatedText(translated);
                    quote.showTranslated();
                }
            }

            Log.d(TAG, "Showing notification");
            NotificationHelper.showQuoteNotification(context, quote);
            if (settingsManager.isNotificationsEnabled()) {
                DailyQuoteScheduler.reschedule(context);
            }
            return Result.success();
        } catch (IOException exception) {
            Log.e(TAG, "Worker failed with IOException", exception);
            return Result.retry();
        }
    }
}

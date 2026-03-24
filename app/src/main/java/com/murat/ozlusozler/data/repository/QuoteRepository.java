package com.murat.ozlusozler.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.murat.ozlusozler.R;
import com.murat.ozlusozler.data.local.AppDatabase;
import com.murat.ozlusozler.data.local.FavoriteQuoteDao;
import com.murat.ozlusozler.data.local.FavoriteQuoteEntity;
import com.murat.ozlusozler.data.model.Quote;
import com.murat.ozlusozler.data.model.QuoteResponse;
import com.murat.ozlusozler.data.network.ApiClient;
import com.murat.ozlusozler.data.network.GeminiManager;
import com.murat.ozlusozler.data.network.QuoteApiService;
import com.murat.ozlusozler.util.CategoryMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuoteRepository {

    public interface QuoteResultCallback {
        void onSuccess(Quote quote);

        void onError(String message);
    }

    public interface FavoriteToggleCallback {
        void onComplete(boolean isFavorite, boolean added);
    }

    public interface FavoriteCheckCallback {
        void onResult(boolean isFavorite);
    }

    public interface CompletionCallback {
        void onSuccess();

        void onError(String message);
    }

    public interface TranslationCallback {
        void onSuccess(Quote quote);

        void onError(String message);
    }

    private static volatile QuoteRepository instance;

    private final Context appContext;
    private final QuoteApiService quoteApiService;
    private final GeminiManager geminiManager;
    private final FavoriteQuoteDao favoriteQuoteDao;
    private final ExecutorService databaseExecutor;
    private final Handler mainHandler;

    private QuoteRepository(Context context) {
        appContext = context.getApplicationContext();
        quoteApiService = ApiClient.getQuoteApiService();
        geminiManager = new GeminiManager();
        favoriteQuoteDao = AppDatabase.getInstance(appContext).favoriteQuoteDao();
        databaseExecutor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static QuoteRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (QuoteRepository.class) {
                if (instance == null) {
                    instance = new QuoteRepository(context);
                }
            }
        }
        return instance;
    }

    public void fetchRandomQuote(QuoteResultCallback callback) {
        quoteApiService.getRandomQuote().enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuoteResponse> call, @NonNull Response<QuoteResponse> response) {
                QuoteResponse body = response.body();
                if (!response.isSuccessful() || body == null || body.getQuote() == null || body.getQuote().trim().isEmpty()) {
                    callback.onError(appContext.getString(R.string.error_quote_load));
                    return;
                }

                String author = body.getAuthor();
                if (author == null || author.trim().isEmpty()) {
                    author = appContext.getString(R.string.unknown_author);
                }

                Quote quote = new Quote(
                        String.valueOf(body.getId()),
                        body.getQuote().trim(),
                        author.trim(),
                        CategoryMapper.CATEGORY_GENERAL
                );

                categorizeQuote(quote, callback);
            }

            @Override
            public void onFailure(@NonNull Call<QuoteResponse> call, @NonNull Throwable throwable) {
                if (throwable instanceof IOException) {
                    callback.onError(appContext.getString(R.string.error_network));
                } else {
                    callback.onError(appContext.getString(R.string.error_quote_load));
                }
            }
        });
    }

    public LiveData<List<FavoriteQuoteEntity>> getAllFavorites() {
        return favoriteQuoteDao.getAllFavorites();
    }

    public void checkIsFavorite(Quote quote, FavoriteCheckCallback callback) {
        if (quote == null) {
            callback.onResult(false);
            return;
        }

        databaseExecutor.execute(() -> {
            boolean exists = favoriteQuoteDao.exists(quote.getOriginalText(), quote.getAuthor());
            mainHandler.post(() -> callback.onResult(exists));
        });
    }

    public void toggleFavorite(Quote quote, FavoriteToggleCallback callback) {
        if (quote == null) {
            callback.onComplete(false, false);
            return;
        }

        databaseExecutor.execute(() -> {
            boolean exists = favoriteQuoteDao.exists(quote.getOriginalText(), quote.getAuthor());
            boolean finalFavorite;
            boolean added;

            if (exists) {
                favoriteQuoteDao.deleteByQuoteAndAuthor(quote.getOriginalText(), quote.getAuthor());
                finalFavorite = false;
                added = false;
            } else {
                favoriteQuoteDao.insert(FavoriteQuoteEntity.fromQuote(quote));
                finalFavorite = true;
                added = true;
            }

            boolean finalAdded = added;
            boolean finalFavoriteState = finalFavorite;
            mainHandler.post(() -> callback.onComplete(finalFavoriteState, finalAdded));
        });
    }

    public void removeFavorite(FavoriteQuoteEntity entity, CompletionCallback callback) {
        if (entity == null) {
            callback.onError(appContext.getString(R.string.error_unknown));
            return;
        }

        databaseExecutor.execute(() -> {
            int deleted = favoriteQuoteDao.deleteById(entity.getId());
            mainHandler.post(() -> {
                if (deleted > 0) {
                    callback.onSuccess();
                } else {
                    callback.onError(appContext.getString(R.string.error_unknown));
                }
            });
        });
    }

    public void translateQuote(Quote quote, TranslationCallback callback) {
        if (quote == null) {
            callback.onError(appContext.getString(R.string.error_unknown));
            return;
        }

        if (!geminiManager.hasApiKey()) {
            callback.onError(appContext.getString(R.string.error_gemini_key_missing));
            return;
        }

        String prompt = appContext.getString(R.string.gemini_translate_prompt_template, quote.getOriginalText());
        geminiManager.generateTextAsync(prompt, new GeminiManager.GeminiTextCallback() {
            @Override
            public void onSuccess(String text) {
                quote.setTranslatedText(text);
                quote.showTranslated();
                callback.onSuccess(quote);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(appContext.getString(R.string.error_translation_failed));
            }
        });
    }

    private void categorizeQuote(Quote quote, QuoteResultCallback callback) {
        if (!geminiManager.hasApiKey()) {
            callback.onSuccess(quote);
            return;
        }

        String prompt = appContext.getString(
                R.string.gemini_prompt_template,
                quote.getOriginalText(),
                quote.getAuthor()
        );
        geminiManager.generateTextAsync(prompt, new GeminiManager.GeminiTextCallback() {
            @Override
            public void onSuccess(String text) {
                quote.setCategoryKey(CategoryMapper.normalizeGeminiCategory(text));
                callback.onSuccess(quote);
            }

            @Override
            public void onError(Throwable throwable) {
                quote.setCategoryKey(CategoryMapper.CATEGORY_GENERAL);
                callback.onSuccess(quote);
            }
        });
    }
}

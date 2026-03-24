package com.murat.ozlusozler.data.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.murat.ozlusozler.BuildConfig;
import com.murat.ozlusozler.data.model.GeminiRequest;
import com.murat.ozlusozler.data.model.GeminiResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeminiManager {

    private static final String TAG = "GeminiManager";

    public interface GeminiTextCallback {
        void onSuccess(String text);

        void onError(Throwable throwable);
    }

    private static final String MODEL_NAME = "gemini-2.5-flash";

    private final GeminiApiService geminiApiService;

    public GeminiManager() {
        geminiApiService = ApiClient.getGeminiApiService();
    }

    public boolean hasApiKey() {
        return BuildConfig.GEMINI_API_KEY != null && !BuildConfig.GEMINI_API_KEY.trim().isEmpty();
    }

    public void generateTextAsync(String prompt, GeminiTextCallback callback) {
        if (!hasApiKey()) {
            Log.e(TAG, "Gemini API key is empty at runtime");
            callback.onError(new IllegalStateException("Gemini API key is missing"));
            return;
        }

        GeminiRequest request = GeminiRequest.fromPrompt(prompt);
        geminiApiService.generateContent(MODEL_NAME, BuildConfig.GEMINI_API_KEY, request)
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeminiResponse> call, @NonNull Response<GeminiResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.e(TAG, "Gemini async request failed. code=" + response.code());
                            callback.onError(new IOException("Gemini request failed with code: " + response.code()));
                            return;
                        }

                        String text = GeminiResponse.extractPrimaryText(response.body());
                        if (text == null || text.trim().isEmpty()) {
                            Log.e(TAG, "Gemini async response text was empty");
                            callback.onError(new IOException("Gemini response was empty"));
                            return;
                        }

                        callback.onSuccess(text.trim());
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeminiResponse> call, @NonNull Throwable throwable) {
                        Log.e(TAG, "Gemini async request crashed", throwable);
                        callback.onError(throwable);
                    }
                });
    }

    public String generateTextSync(String prompt) throws IOException {
        if (!hasApiKey()) {
            Log.e(TAG, "Gemini API key is empty at runtime");
            throw new IOException("Gemini API key is missing");
        }

        Response<GeminiResponse> response = geminiApiService
                .generateContent(MODEL_NAME, BuildConfig.GEMINI_API_KEY, GeminiRequest.fromPrompt(prompt))
                .execute();

        if (!response.isSuccessful() || response.body() == null) {
            Log.e(TAG, "Gemini sync request failed. code=" + response.code());
            throw new IOException("Gemini request failed with code: " + response.code());
        }

        String text = GeminiResponse.extractPrimaryText(response.body());
        if (text == null || text.trim().isEmpty()) {
            Log.e(TAG, "Gemini sync response text was empty");
            throw new IOException("Gemini response was empty");
        }

        return text.trim();
    }
}

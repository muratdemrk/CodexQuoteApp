package com.murat.ozlusozler.data.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static final String QUOTE_BASE_URL = "https://dummyjson.com/";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/";

    private static Retrofit quoteRetrofit;
    private static Retrofit geminiRetrofit;

    private ApiClient() {
    }

    public static QuoteApiService getQuoteApiService() {
        if (quoteRetrofit == null) {
            quoteRetrofit = new Retrofit.Builder()
                    .baseUrl(QUOTE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return quoteRetrofit.create(QuoteApiService.class);
    }

    public static GeminiApiService getGeminiApiService() {
        if (geminiRetrofit == null) {
            geminiRetrofit = new Retrofit.Builder()
                    .baseUrl(GEMINI_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geminiRetrofit.create(GeminiApiService.class);
    }
}


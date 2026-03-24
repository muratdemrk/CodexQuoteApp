package com.murat.ozlusozler.data.network;

import com.murat.ozlusozler.data.model.QuoteResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {

    @GET("quotes/random")
    Call<QuoteResponse> getRandomQuote();
}


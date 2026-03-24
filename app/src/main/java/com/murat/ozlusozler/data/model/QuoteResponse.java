package com.murat.ozlusozler.data.model;

import com.google.gson.annotations.SerializedName;

public class QuoteResponse {

    @SerializedName("id")
    private int id;

    @SerializedName("quote")
    private String quote;

    @SerializedName("author")
    private String author;

    public int getId() {
        return id;
    }

    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }
}


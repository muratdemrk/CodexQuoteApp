package com.murat.ozlusozler.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.murat.ozlusozler.data.model.Quote;

@Entity(
        tableName = "favorite_quotes",
        indices = {
                @Index(value = {"originalText", "author"}, unique = true)
        }
)
public class FavoriteQuoteEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String sourceId;
    private String originalText;
    private String quoteText;
    private String author;
    private String categoryKey;
    private long addedAt;

    public FavoriteQuoteEntity(String sourceId, String originalText, String quoteText,
                               String author, String categoryKey, long addedAt) {
        this.sourceId = sourceId;
        this.originalText = originalText;
        this.quoteText = quoteText;
        this.author = author;
        this.categoryKey = categoryKey;
        this.addedAt = addedAt;
    }

    public static FavoriteQuoteEntity fromQuote(Quote quote) {
        return new FavoriteQuoteEntity(
                quote.getSourceId(),
                quote.getOriginalText(),
                quote.getText(),
                quote.getAuthor(),
                quote.getCategoryKey(),
                System.currentTimeMillis()
        );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getQuoteText() {
        return quoteText;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public long getAddedAt() {
        return addedAt;
    }
}

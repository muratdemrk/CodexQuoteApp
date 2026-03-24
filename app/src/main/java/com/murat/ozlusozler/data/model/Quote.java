package com.murat.ozlusozler.data.model;

public class Quote {

    private final String sourceId;
    private final String originalText;
    private final String author;
    private String categoryKey;
    private String translatedText;
    private boolean showingTranslated;

    public Quote(String sourceId, String originalText, String author, String categoryKey) {
        this.sourceId = sourceId;
        this.originalText = originalText;
        this.author = author;
        this.categoryKey = categoryKey;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getText() {
        if (showingTranslated && hasTranslation()) {
            return translatedText;
        }
        return originalText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public boolean hasTranslation() {
        return translatedText != null && !translatedText.trim().isEmpty();
    }

    public boolean isShowingTranslated() {
        return showingTranslated && hasTranslation();
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
        this.showingTranslated = hasTranslation();
    }

    public void showOriginal() {
        showingTranslated = false;
    }

    public void showTranslated() {
        if (hasTranslation()) {
            showingTranslated = true;
        }
    }
}

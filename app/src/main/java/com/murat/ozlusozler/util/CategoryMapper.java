package com.murat.ozlusozler.util;

import android.content.Context;

import androidx.annotation.NonNull;

import com.murat.ozlusozler.R;

import java.text.Normalizer;
import java.util.Locale;

public final class CategoryMapper {

    public static final String CATEGORY_MOTIVATION = "Motivation";
    public static final String CATEGORY_SUCCESS = "Success";
    public static final String CATEGORY_LOVE = "Love";
    public static final String CATEGORY_LIFE = "Life";
    public static final String CATEGORY_WISDOM = "Wisdom";
    public static final String CATEGORY_FRIENDSHIP = "Friendship";
    public static final String CATEGORY_PATIENCE = "Patience";
    public static final String CATEGORY_HOPE = "Hope";
    public static final String CATEGORY_EDUCATION = "Education";
    public static final String CATEGORY_JUSTICE = "Justice";
    public static final String CATEGORY_TIME = "Time";
    public static final String CATEGORY_HUMANITY = "Humanity";
    public static final String CATEGORY_GENERAL = "General";

    private CategoryMapper() {
    }

    @NonNull
    public static String normalizeGeminiCategory(String rawCategory) {
        if (rawCategory == null || rawCategory.trim().isEmpty()) {
            return CATEGORY_GENERAL;
        }

        String firstLine = rawCategory.trim().split("\\R")[0]
                .replace("\"", "")
                .replace(".", "")
                .trim();
        String normalized = slugify(firstLine);

        switch (normalized) {
            case "motivation":
            case "motivasyon":
                return CATEGORY_MOTIVATION;
            case "success":
            case "basari":
                return CATEGORY_SUCCESS;
            case "love":
            case "sevgi":
                return CATEGORY_LOVE;
            case "life":
            case "hayat":
                return CATEGORY_LIFE;
            case "wisdom":
            case "bilgelik":
                return CATEGORY_WISDOM;
            case "friendship":
            case "dostluk":
                return CATEGORY_FRIENDSHIP;
            case "patience":
            case "sabir":
                return CATEGORY_PATIENCE;
            case "hope":
            case "umut":
                return CATEGORY_HOPE;
            case "education":
            case "egitim":
                return CATEGORY_EDUCATION;
            case "justice":
            case "adalet":
                return CATEGORY_JUSTICE;
            case "time":
            case "zaman":
                return CATEGORY_TIME;
            case "humanity":
            case "insanlik":
                return CATEGORY_HUMANITY;
            default:
                return CATEGORY_GENERAL;
        }
    }

    @NonNull
    public static String getDisplayCategory(Context context, String categoryKey) {
        switch (normalizeGeminiCategory(categoryKey)) {
            case CATEGORY_MOTIVATION:
                return context.getString(R.string.category_motivation);
            case CATEGORY_SUCCESS:
                return context.getString(R.string.category_success);
            case CATEGORY_LOVE:
                return context.getString(R.string.category_love);
            case CATEGORY_LIFE:
                return context.getString(R.string.category_life);
            case CATEGORY_WISDOM:
                return context.getString(R.string.category_wisdom);
            case CATEGORY_FRIENDSHIP:
                return context.getString(R.string.category_friendship);
            case CATEGORY_PATIENCE:
                return context.getString(R.string.category_patience);
            case CATEGORY_HOPE:
                return context.getString(R.string.category_hope);
            case CATEGORY_EDUCATION:
                return context.getString(R.string.category_education);
            case CATEGORY_JUSTICE:
                return context.getString(R.string.category_justice);
            case CATEGORY_TIME:
                return context.getString(R.string.category_time);
            case CATEGORY_HUMANITY:
                return context.getString(R.string.category_humanity);
            default:
                return context.getString(R.string.category_general);
        }
    }

    private static String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase(Locale.US).replaceAll("[^a-z]", "");
    }
}


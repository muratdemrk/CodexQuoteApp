package com.murat.ozlusozler.util;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemeUtils {

    private ThemeUtils() {
    }

    public static void applyTheme(boolean darkModeEnabled) {
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}


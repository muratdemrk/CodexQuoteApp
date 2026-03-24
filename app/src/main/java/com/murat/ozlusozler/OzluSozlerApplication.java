package com.murat.ozlusozler;

import android.app.Application;
import android.content.res.Configuration;

import com.murat.ozlusozler.util.DailyQuoteScheduler;
import com.murat.ozlusozler.util.NotificationHelper;
import com.murat.ozlusozler.util.SettingsManager;
import com.murat.ozlusozler.util.ThemeUtils;

import java.util.Locale;

public class OzluSozlerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SettingsManager settingsManager = new SettingsManager(this);
        applyLocale(settingsManager.getLanguage());
        ThemeUtils.applyTheme(settingsManager.isDarkModeEnabled());
        NotificationHelper.createNotificationChannel(this);

        if (settingsManager.isNotificationsEnabled() && NotificationHelper.hasNotificationPermission(this)) {
            DailyQuoteScheduler.ensureScheduled(this);
        }
    }

    private void applyLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}

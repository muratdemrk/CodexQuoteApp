package com.murat.ozlusozler.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {

    public static final String NOTIFICATION_LANGUAGE_EN = "en";
    public static final String NOTIFICATION_LANGUAGE_TR = "tr";

    private static final String PREF_NAME = "ozlu_sozler_preferences";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_NOTIFICATION_HOUR = "notification_hour";
    private static final String KEY_NOTIFICATION_MINUTE = "notification_minute";
    private static final String KEY_NOTIFICATION_PERMISSION_ASKED = "notification_permission_asked";
    private static final String KEY_AUTO_TRANSLATE_TURKISH = "auto_translate_turkish";
    private static final String KEY_NOTIFICATION_LANGUAGE = "notification_language";
    private static final String KEY_LANGUAGE = "app_language";

    private final SharedPreferences sharedPreferences;

    public SettingsManager(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public int getNotificationHour() {
        return sharedPreferences.getInt(KEY_NOTIFICATION_HOUR, 9);
    }

    public int getNotificationMinute() {
        return sharedPreferences.getInt(KEY_NOTIFICATION_MINUTE, 0);
    }

    public void setNotificationTime(int hour, int minute) {
        sharedPreferences.edit()
                .putInt(KEY_NOTIFICATION_HOUR, hour)
                .putInt(KEY_NOTIFICATION_MINUTE, minute)
                .apply();
    }

    public boolean hasAskedNotificationPermission() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_PERMISSION_ASKED, false);
    }

    public void setNotificationPermissionAsked(boolean asked) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_PERMISSION_ASKED, asked).apply();
    }

    public boolean isAutoTranslateTurkishEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_TRANSLATE_TURKISH, false);
    }

    public void setAutoTranslateTurkishEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_AUTO_TRANSLATE_TURKISH, enabled).apply();
    }

    public String getNotificationLanguage() {
        return sharedPreferences.getString(KEY_NOTIFICATION_LANGUAGE, NOTIFICATION_LANGUAGE_EN);
    }

    public void setNotificationLanguage(String languageCode) {
        sharedPreferences.edit().putString(KEY_NOTIFICATION_LANGUAGE, languageCode).apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(KEY_LANGUAGE, "tr");
    }

    public void setLanguage(String languageCode) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }
}

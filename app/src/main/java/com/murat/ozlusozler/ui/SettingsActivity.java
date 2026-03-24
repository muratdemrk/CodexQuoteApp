package com.murat.ozlusozler.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.murat.ozlusozler.R;
import com.murat.ozlusozler.databinding.ActivitySettingsBinding;
import com.murat.ozlusozler.util.DailyQuoteScheduler;
import com.murat.ozlusozler.util.NotificationHelper;
import com.murat.ozlusozler.util.SettingsManager;
import com.murat.ozlusozler.util.ThemeUtils;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SettingsManager settingsManager;
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private boolean ignoreNotificationToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsManager = new SettingsManager(this);

        setupToolbar();
        setupPermissionLauncher();
        bindCurrentValues();
        setupListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void setupPermissionLauncher() {
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        settingsManager.setNotificationsEnabled(true);
                        binding.switchNotifications.setChecked(true);
                        DailyQuoteScheduler.reschedule(this);
                        showMessage(getString(R.string.notifications_enabled));
                    } else {
                        ignoreNotificationToggle = true;
                        settingsManager.setNotificationsEnabled(false);
                        binding.switchNotifications.setChecked(false);
                        DailyQuoteScheduler.cancel(this);
                        showMessage(getString(R.string.notification_permission_denied));
                    }
                }
        );
    }

    private void bindCurrentValues() {
        binding.switchDarkMode.setChecked(settingsManager.isDarkModeEnabled());
        binding.switchNotifications.setChecked(settingsManager.isNotificationsEnabled());
        binding.switchAutoTranslate.setChecked(settingsManager.isAutoTranslateTurkishEnabled());

        String currentLang = settingsManager.getLanguage();
        binding.toggleLanguage.check("tr".equals(currentLang) ? R.id.btnLangTr : R.id.btnLangEn);

        String notificationLanguage = settingsManager.getNotificationLanguage();
        binding.toggleNotificationLanguage.check(
                SettingsManager.NOTIFICATION_LANGUAGE_TR.equals(notificationLanguage)
                        ? R.id.btnNotificationLangTr
                        : R.id.btnNotificationLangEn
        );

        updateTimeText();
    }

    private void setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingsManager.setDarkModeEnabled(isChecked);
            ThemeUtils.applyTheme(isChecked);
        });

        binding.toggleLanguage.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }

            String newLang = (checkedId == R.id.btnLangTr) ? "tr" : "en";
            if (!newLang.equals(settingsManager.getLanguage())) {
                settingsManager.setLanguage(newLang);
                applyLanguage(newLang);
            }
        });

        binding.switchAutoTranslate.setOnCheckedChangeListener((buttonView, isChecked) ->
                settingsManager.setAutoTranslateTurkishEnabled(isChecked)
        );

        binding.toggleNotificationLanguage.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }

            String notificationLanguage = checkedId == R.id.btnNotificationLangTr
                    ? SettingsManager.NOTIFICATION_LANGUAGE_TR
                    : SettingsManager.NOTIFICATION_LANGUAGE_EN;
            settingsManager.setNotificationLanguage(notificationLanguage);

            if (settingsManager.isNotificationsEnabled() && NotificationHelper.hasNotificationPermission(this)) {
                DailyQuoteScheduler.reschedule(this);
            }
        });

        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ignoreNotificationToggle) {
                ignoreNotificationToggle = false;
                return;
            }

            if (isChecked) {
                enableNotifications();
            } else {
                settingsManager.setNotificationsEnabled(false);
                DailyQuoteScheduler.cancel(this);
                showMessage(getString(R.string.notifications_disabled));
            }
        });

        binding.buttonNotificationTime.setOnClickListener(view -> openTimePicker());
    }

    private void applyLanguage(String langCode) {
        LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(langCode);
        AppCompatDelegate.setApplicationLocales(appLocales);
    }

    private void enableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            return;
        }

        settingsManager.setNotificationsEnabled(true);
        DailyQuoteScheduler.reschedule(this);
        showMessage(getString(R.string.notifications_enabled));
    }

    private void openTimePicker() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(DateFormat.is24HourFormat(this) ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                .setHour(settingsManager.getNotificationHour())
                .setMinute(settingsManager.getNotificationMinute())
                .setTitleText(R.string.notification_time_title)
                .build();

        picker.addOnPositiveButtonClickListener(view -> {
            settingsManager.setNotificationTime(picker.getHour(), picker.getMinute());
            updateTimeText();
            if (settingsManager.isNotificationsEnabled() && NotificationHelper.hasNotificationPermission(this)) {
                DailyQuoteScheduler.reschedule(this);
            }
        });

        picker.show(getSupportFragmentManager(), "notification_time_picker");
    }

    private void updateTimeText() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, settingsManager.getNotificationHour());
        calendar.set(Calendar.MINUTE, settingsManager.getNotificationMinute());
        calendar.set(Calendar.SECOND, 0);

        String formatted = DateFormat.getTimeFormat(this).format(calendar.getTime());
        binding.textSelectedTime.setText(getString(R.string.current_time_format, formatted));
    }

    private void showMessage(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}

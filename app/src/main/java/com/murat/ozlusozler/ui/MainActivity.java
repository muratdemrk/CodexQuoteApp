package com.murat.ozlusozler.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.murat.ozlusozler.R;
import com.murat.ozlusozler.data.model.Quote;
import com.murat.ozlusozler.databinding.ActivityMainBinding;
import com.murat.ozlusozler.ui.viewmodel.MainViewModel;
import com.murat.ozlusozler.ui.viewmodel.MainViewModelFactory;
import com.murat.ozlusozler.util.CategoryMapper;
import com.murat.ozlusozler.util.DailyQuoteScheduler;
import com.murat.ozlusozler.util.EventObserver;
import com.murat.ozlusozler.util.NotificationHelper;
import com.murat.ozlusozler.util.SettingsManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private SettingsManager settingsManager;
    private ActivityResultLauncher<String> notificationPermissionLauncher;
    private boolean isLoading;
    private boolean isTranslating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsManager = new SettingsManager(this);
        viewModel = new ViewModelProvider(this, new MainViewModelFactory(getApplication()))
                .get(MainViewModel.class);

        setupNotificationPermissionLauncher();
        setupToolbarAndDrawer();
        setupActions();
        setupObservers();
        requestNotificationPermissionIfNeeded();

        viewModel.loadInitialQuote();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (settingsManager.isNotificationsEnabled() && NotificationHelper.hasNotificationPermission(this)) {
            DailyQuoteScheduler.ensureScheduled(this);
        }
    }

    private void setupNotificationPermissionLauncher() {
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted && settingsManager.isNotificationsEnabled()) {
                        DailyQuoteScheduler.ensureScheduled(this);
                    } else if (!granted) {
                        settingsManager.setNotificationsEnabled(false);
                        DailyQuoteScheduler.cancel(this);
                        Snackbar.make(
                                binding.getRoot(),
                                getString(R.string.notification_permission_denied),
                                Snackbar.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void setupToolbarAndDrawer() {
        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.toolbar_navigation,
                R.string.toolbar_navigation
        );
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.setCheckedItem(R.id.nav_home);
    }

    private void setupActions() {
        binding.buttonFavorite.setEnabled(false);
        binding.buttonTranslate.setEnabled(false);
        binding.buttonRefresh.setOnClickListener(view -> viewModel.loadRandomQuote());
        binding.buttonFavorite.setOnClickListener(view -> viewModel.toggleFavorite());
        binding.buttonTranslate.setOnClickListener(view -> viewModel.onTranslateButtonClicked());
    }

    private void setupObservers() {
        viewModel.getCurrentQuote().observe(this, this::renderQuote);
        viewModel.isLoading().observe(this, loading -> {
            isLoading = Boolean.TRUE.equals(loading);
            renderBusyState();
        });
        viewModel.isTranslating().observe(this, translating -> {
            isTranslating = Boolean.TRUE.equals(translating);
            renderBusyState();
        });
        viewModel.getFavoriteState().observe(this, this::renderFavoriteState);
        viewModel.getMessageEvent().observe(this, new EventObserver<String>() {
            @Override
            public void onEventUnhandledContent(@NonNull String value) {
                Snackbar.make(binding.getRoot(), value, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (settingsManager.isNotificationsEnabled()) {
                DailyQuoteScheduler.ensureScheduled(this);
            }
            return;
        }

        if (!settingsManager.hasAskedNotificationPermission()
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            settingsManager.setNotificationPermissionAsked(true);
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else if (settingsManager.isNotificationsEnabled()
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            DailyQuoteScheduler.ensureScheduled(this);
        }
    }

    private void renderQuote(Quote quote) {
        if (quote == null) {
            return;
        }

        binding.textQuote.setText(quote.getText());
        binding.textAuthor.setText(getString(R.string.quote_author_prefix, quote.getAuthor()));
        binding.textCategory.setText(getString(
                R.string.quote_category_prefix,
                CategoryMapper.getDisplayCategory(this, quote.getCategoryKey())
        ));
        binding.buttonTranslate.setText(
                quote.isShowingTranslated() ? R.string.show_original_quote : R.string.translate_to_turkish
        );
        binding.quoteCard.setContentDescription(getString(R.string.quote_card_content_description));
        binding.quoteCard.setAlpha(0f);
        binding.quoteCard.animate().alpha(1f).setDuration(220L).start();
        renderBusyState();
    }

    private void renderBusyState() {
        boolean busy = isLoading || isTranslating;
        binding.progressIndicator.setVisibility(busy ? View.VISIBLE : View.GONE);
        binding.buttonRefresh.setEnabled(!busy);
        binding.buttonFavorite.setEnabled(!busy && viewModel.getCurrentQuote().getValue() != null);
        binding.buttonTranslate.setEnabled(!busy && viewModel.getCurrentQuote().getValue() != null);
    }

    private void renderFavoriteState(Boolean isFavorite) {
        boolean favorite = Boolean.TRUE.equals(isFavorite);
        binding.buttonFavorite.setIconResource(
                favorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline
        );
        binding.buttonFavorite.setText(
                favorite ? R.string.favorite_action_remove : R.string.favorite_action_add
        );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}

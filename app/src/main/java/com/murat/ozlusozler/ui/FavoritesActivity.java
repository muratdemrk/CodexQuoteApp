package com.murat.ozlusozler.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.murat.ozlusozler.data.local.FavoriteQuoteEntity;
import com.murat.ozlusozler.databinding.ActivityFavoritesBinding;
import com.murat.ozlusozler.ui.adapter.FavoriteQuoteAdapter;
import com.murat.ozlusozler.ui.viewmodel.FavoritesViewModel;
import com.murat.ozlusozler.ui.viewmodel.FavoritesViewModelFactory;
import com.murat.ozlusozler.util.EventObserver;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ActivityFavoritesBinding binding;
    private FavoritesViewModel viewModel;
    private FavoriteQuoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new FavoritesViewModelFactory(getApplication()))
                .get(FavoritesViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupObservers();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void setupRecyclerView() {
        adapter = new FavoriteQuoteAdapter(viewModel::removeFavorite);
        binding.recyclerFavorites.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerFavorites.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getFavorites().observe(this, this::renderFavorites);
        viewModel.getMessageEvent().observe(this, new EventObserver<String>() {
            @Override
            public void onEventUnhandledContent(@NonNull String value) {
                Snackbar.make(binding.getRoot(), value, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void renderFavorites(List<FavoriteQuoteEntity> favorites) {
        adapter.submitList(favorites);
        boolean empty = favorites == null || favorites.isEmpty();
        binding.layoutEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerFavorites.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}

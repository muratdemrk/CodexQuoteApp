package com.murat.ozlusozler.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FavoritesViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public FavoritesViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FavoritesViewModel.class)) {
            return (T) new FavoritesViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}


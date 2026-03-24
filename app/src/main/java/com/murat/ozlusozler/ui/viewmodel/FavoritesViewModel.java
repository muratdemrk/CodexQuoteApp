package com.murat.ozlusozler.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.murat.ozlusozler.R;
import com.murat.ozlusozler.data.local.FavoriteQuoteEntity;
import com.murat.ozlusozler.data.repository.QuoteRepository;
import com.murat.ozlusozler.util.Event;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {

    private final QuoteRepository repository;
    private final LiveData<List<FavoriteQuoteEntity>> favorites;
    private final MutableLiveData<Event<String>> messageEvent = new MutableLiveData<>();

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repository = QuoteRepository.getInstance(application);
        favorites = repository.getAllFavorites();
    }

    public LiveData<List<FavoriteQuoteEntity>> getFavorites() {
        return favorites;
    }

    public LiveData<Event<String>> getMessageEvent() {
        return messageEvent;
    }

    public void removeFavorite(FavoriteQuoteEntity entity) {
        repository.removeFavorite(entity, new QuoteRepository.CompletionCallback() {
            @Override
            public void onSuccess() {
                messageEvent.setValue(new Event<>(getApplication().getString(R.string.favorite_removed)));
            }

            @Override
            public void onError(String message) {
                messageEvent.setValue(new Event<>(message));
            }
        });
    }
}

